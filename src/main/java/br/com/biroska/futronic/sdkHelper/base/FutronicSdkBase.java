package br.com.biroska.futronic.sdkHelper.base;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import br.com.biroska.futronic.sdkHelper.callbacks.ICallBack;
import br.com.biroska.futronic.sdkHelper.data.identification.FtrIdentifyRecord;
import br.com.biroska.futronic.sdkHelper.data.identification.FtrIdentifyResult;
import br.com.biroska.futronic.sdkHelper.data.proccess.FTR_PROGRESS;
import br.com.biroska.futronic.sdkHelper.enums.EnrollmentState;
import br.com.biroska.futronic.sdkHelper.enums.FarnValues;
import br.com.biroska.futronic.sdkHelper.enums.VersionCompatible;
import br.com.biroska.futronic.sdkHelper.exception.FutronicException;

/**
 * Base class for any Java-wrapper class. It initialize and terminate the FTRAPI.dll library.
 *
 * @author Shustikov
 */
public abstract class FutronicSdkBase
{
    /**
     * Base value for the error codes.
     */
    private static final int FTR_RETCODE_ERROR_BASE = 1;
    
    /**
     * Base value for the device error codes.
     */
    private static final int FTR_RETCODE_DEVICE_BASE = 200;

    // Futronic API return code

    public static final int RETCODE_OK = 0;
    public static final int RETCODE_NO_MEMORY = (FTR_RETCODE_ERROR_BASE + 1);
    public static final int RETCODE_INVALID_ARG = (FTR_RETCODE_ERROR_BASE + 2);
    public static final int RETCODE_ALREADY_IN_USE = (FTR_RETCODE_ERROR_BASE + 3);
    public static final int RETCODE_INVALID_PURPOSE = (FTR_RETCODE_ERROR_BASE + 4);
    public static final int RETCODE_INTERNAL_ERROR = (FTR_RETCODE_ERROR_BASE + 5);

    public static final int RETCODE_UNABLE_TO_CAPTURE = (FTR_RETCODE_ERROR_BASE + 6);
    public static final int RETCODE_CANCELED_BY_USER = (FTR_RETCODE_ERROR_BASE + 7);
    public static final int RETCODE_NO_MORE_RETRIES = (FTR_RETCODE_ERROR_BASE + 8);
    public static final int RETCODE_INCONSISTENT_SAMPLING = (FTR_RETCODE_ERROR_BASE + 10);
    public static final int RETCODE_TRIAL_EXPIRED = (FTR_RETCODE_ERROR_BASE + 11);

    public static final int RETCODE_FRAME_SOURCE_NOT_SET = (FTR_RETCODE_DEVICE_BASE + 1);
    public static final int RETCODE_DEVICE_NOT_CONNECTED = (FTR_RETCODE_DEVICE_BASE + 2);
    public static final int RETCODE_DEVICE_FAILURE = (FTR_RETCODE_DEVICE_BASE + 3);
    public static final int RETCODE_EMPTY_FRAME = (FTR_RETCODE_DEVICE_BASE + 4);
    public static final int RETCODE_FAKE_SOURCE = (FTR_RETCODE_DEVICE_BASE + 5);
    public static final int RETCODE_INCOMPATIBLE_HARDWARE = (FTR_RETCODE_DEVICE_BASE + 6);
    public static final int RETCODE_INCOMPATIBLE_FIRMWARE = (FTR_RETCODE_DEVICE_BASE + 7);
    public static final int RETCODE_FRAME_SOURCE_CHANGED = (FTR_RETCODE_DEVICE_BASE + 8);

    // Signal values.

    /**
     * Invitation for touching the fingerprint scanner surface.
     */
    public static final int FTR_SIGNAL_TOUCH_SENSOR = 1;
    
    /**
     * Proposal to take off a finger from the scanner surface.
     */
    public static final int FTR_SIGNAL_TAKE_OFF = 2;
    public static final int FTR_SIGNAL_FAKE_SOURCE = 3;

    // State bit mask values.
  
    /**
     * The pBitmap parameter provided
     */
    public static final int FTR_STATE_FRAME_PROVIDED = 0x01;
    
    /**
     * The pBitmap parameter provided
     */
    public static final int FTR_STATE_SIGNAL_PROVIDED = 0x02;
    
    // Response values.

    /**
     * The calling function must return control as quickly as possible. The 
     * caller returns the RETCODE_CANCELED_BY_USER value.
     */
    public static final int FTR_CANCEL = 1;
    
    /**
     * The calling function can continue execution.
     */
    public static final int FTR_CONTINUE = 2;

    // Available frame sources.
    
    /**
     * No device attached
     */
    public static final int FSD_UNDEFINED = 0;
    
    /**
     * Futronic USB Fingerprint Scanner Device.
     */
    public static final int FSD_FUTRONIC_USB = 1;

    /**
     * Contains predefined FAR values. This array must have the same size as FarnValues 
     * without farn_custom (currently only 6 elements).
     */
    public static int[] rgFARN = {1,        // 738151462: 0,343728560
                                  95,       //  20854379: 0,009711077
                                  166,      //    103930: 0,000048396
                                  245,      //       256: 0,000000119209
                                  345,      //         8: 0,000000003725
                                  405       //         1: 0,000000000466
    };
    
    // Values used for the version definition.
    public static final int FTR_VERSION_PREVIOUS = 1;
    public static final int FTR_VERSION_COMPATIBLE = 2;
    public static final int FTR_VERSION_CURRENT = 3;

    /**
     * Number of the FTRAPI library references.
     */
    private static int m_RefCount = 0;

    /**
     * This object prevents more than one thread from using nRefCount simultaneously.
     * It also synchronize the FTRAPI library initialization/deinitialization.
     */
    private static Object m_InitLock = new Object();

    /**
     * This object synchronizes the FTRAPI.dll usage from any Java-wrapper class.
     */
    public static Object m_SyncRoot = new Object();
    
    static
    {
        System.loadLibrary( "ftrJSDK" );
    }

    /**
     * Gets an error description by a Futronic SDK error code.
     *
     * @param nRetCode Futronic SDK error code.
     *
     * @return Error description.
     */
    public static String SdkRetCode2Message(int nRetCode)
    {
        String szMessage;
        switch (nRetCode)
        {
        case RETCODE_OK:
            szMessage = new String( "The function is completed successfully." );
            break;

        case RETCODE_NO_MEMORY:
            szMessage = new String( "There is not enough memory to continue the execution of a program." );
            break;

        case RETCODE_INVALID_ARG:
            szMessage = new String( "Some parameters were not specified or had invalid values.");
            break;

        case RETCODE_ALREADY_IN_USE:
            szMessage = new String( "The current operation has already initialized the API." );
            break;

        case RETCODE_INVALID_PURPOSE:
            szMessage = new String( "Base template is not correspond purpose.");
            break;

        case RETCODE_INTERNAL_ERROR:
            szMessage = new String( "Internal SDK or Win32 API system error.");
            break;

        case RETCODE_UNABLE_TO_CAPTURE:
            szMessage = new String( "Unable to capture." );
            break;

        case RETCODE_CANCELED_BY_USER:
            szMessage = new String( "User canceled operation." );
            break;

        case RETCODE_NO_MORE_RETRIES:
            szMessage = new String( "Number of retries is overflow." );
            break;

        case RETCODE_INCONSISTENT_SAMPLING:
            szMessage = new String( "Source sampling is inconsistent." );
            break;

        case RETCODE_FRAME_SOURCE_NOT_SET:
            szMessage = new String( "Frame source not set." );
            break;

        case RETCODE_DEVICE_NOT_CONNECTED:
            szMessage = new String( "The frame source device is not connected." );
            break;

        case RETCODE_DEVICE_FAILURE:
            szMessage = new String( "Device failure." );
            break;

        case RETCODE_EMPTY_FRAME:
            szMessage = new String( "Empty frame." );
            break;

        case RETCODE_FAKE_SOURCE:
            szMessage = new String( "Fake source." );
            break;

        case RETCODE_INCOMPATIBLE_HARDWARE:
            szMessage = new String( "Incompatible hardware." );
            break;

        case RETCODE_INCOMPATIBLE_FIRMWARE:
            szMessage = new String( "Incompatible firmware." );
            break;

        case RETCODE_TRIAL_EXPIRED:
            szMessage = new String( "Trial limitation - only 1000 templates may be verified/identified." );
            break;

        case RETCODE_FRAME_SOURCE_CHANGED:
            szMessage = new String( "Frame source has been changed." );
            break;

        default:
            szMessage = String.format( "Unknown error code %d.", nRetCode );
            break;
        }

        return szMessage;
    }
        
    /** 
     * Creates a new instance of FutronicSdkBase 
     *
     * @exception  FutronicException Error occur during SDK initialization. To 
     * get error code, see <code>getErrorCode</code> of FutronicException class.
     */
    public FutronicSdkBase() 
        throws FutronicException
    {
        synchronized ( m_InitLock )
        {
            if( m_RefCount == 0)
            {
                int nResult;
                nResult = FutronicInitialize();
                if( nResult != RETCODE_OK )
                {
                    throw new FutronicException( nResult, SdkRetCode2Message( nResult ) );
                }
            }
            m_RefCount++;
        }
        m_bDispose = false;
        m_bFakeDetection = false;
        m_bFFDControl = true;
        m_bCancel = true;
        m_FarnLevel = FarnValues.farn_normal;
        m_Version = VersionCompatible.ftr_version_current;
        m_bFastMode = false;
        m_InternalVersion = FTR_VERSION_CURRENT;
        m_FARN = rgFARN[ m_FarnLevel.ordinal() ];
        m_State = EnrollmentState.ready_to_process;
        m_WorkedThread = null;
    }
    
    /**
     * This function should be called to abort current process (enrollment, 
     * identification etc.).
     */
    public void OnCalcel()
    {
        m_bCancel = true;
    }

    /**
     * get the "Fake Detection" value
     *
     * @exception IllegalStateException the object disposed.
     */
    public boolean getFakeDetection()
        throws IllegalStateException
    {
        CheckDispose();
        return m_bFakeDetection;
    }

    /**
     * set the "Fake Detection" value
     *
     * Set to <code>true</code>, if you want to activate Live Finger Detection 
     * (LFD) feature during the capture process. The capture time is increasing,
     * when you activate the LFD feature.
     *
     * @param bFakeDetection new value
     *
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation or the object disposed.
     */
    public void setFakeDetection( boolean bFakeDetection )
        throws IllegalStateException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation" );
        m_bFakeDetection = bFakeDetection;
    }

    /**
     * get the "Fake Detection Event Handler" property value
     *
     * @exception IllegalStateException the object disposed.
     */
    public boolean getFFDControl()
        throws IllegalStateException
    {
        CheckDispose();
        return m_bFFDControl;
    }

    /**
     * set the "Fake Detection Event Handler" property value
     * 
     * Set to <code>true</code>, if you want to receive the "Fake Detect" event.
     * You should also set the <code>m_bFakeDetection</code> property to receive
     * this event.
     *
     * @param bFFDControl new value
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation or the object disposed.
     */
    public void setFFDControl( boolean bFFDControl )
        throws IllegalStateException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation" );
        m_bFFDControl = bFFDControl;
    }

    /**
     * get the "False Accepting Ratio" property level
     *
     * @exception IllegalStateException the object disposed.
     */
    public FarnValues getFARnLevel()
        throws IllegalStateException
    {
        CheckDispose();
        return m_FarnLevel;
    }

    /**
     * set the "False Accepting Ratio" property level
     *
     * You cannot use the <code>farn_custom value</code> to set this property. 
     * The <code>farn_custom</code> value shows that a custom value is assigned.
     *
     * @param FarnLevel new level
     *
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation or the object disposed.
     * @exception IllegalArgumentException the argument FarnLevel has invalid 
     * value.
     */
    public void setFARnLevel( FarnValues FarnLevel )
        throws IllegalStateException, IllegalArgumentException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation" );
        if( FarnLevel.ordinal() > rgFARN.length )
            throw new IllegalArgumentException( "The argument FarnLevel has invalid value" );
        m_FarnLevel = FarnLevel;
        m_FARN = rgFARN[ m_FarnLevel.ordinal() ];
    }

    /**
     * get the "False Accepting Ratio" property value
     *
     * @exception IllegalStateException the object disposed.
     */
    public int getFARN()
        throws IllegalStateException
    {
        CheckDispose();
        return m_FARN;
    }

    /**
     * set the "False Accepting Ratio" property level
     *
     * You can set any valid False Accepting Ratio (FAR). The value must be 
     * between 1 and 1000. The larger value implies the "softer" result. If you 
     * set one from FarnValues values, FARnLevel sets to the appropriate level.
     *
     * @param Value new value
     *
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation or the object disposed.
     * @exception IllegalArgumentException the argument Value has invalid 
     * value.
     */
    public void setFARN( int Value )
        throws IllegalStateException, IllegalArgumentException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation" );
        if( Value < 1 || Value > 1000 )
            throw new IllegalArgumentException( "The argument Value has invalid value" );
        m_FarnLevel = FarnValues.farn_custom;
        for( int i = 0; i < rgFARN.length; i++)
        {
            if( rgFARN[i] == Value )
            {
                m_FarnLevel = FarnValues.values()[i];
                break;
            }
        }
        m_FARN = Value;
    }


    /**
     * Gets a value that indicates whether a library is trial version.
     * 
     * @return <code>true<\ccode> if this is a trial version otherwise <c>false<c>
     * @exception  IllegalStateException The class instance is disposed. Any 
     * calls are prohibited.
     */
    public boolean IsTrial()
    {
        CheckDispose();
        return FutronicIsTrial();
    }

    /**
     * Gets a value that specify identification limit value.
     * 
     * @return identification limit value. If property contains Integer.MAX_VALUE 
     * that is "no limits"
     * 
     * @exception  IllegalStateException The class instance is disposed. Any 
     * calls are prohibited.
     */
    public int getIdentificationsLeft()
    {
        CheckDispose();
        return FutronicIdentificationsLeft();
    }

    /**
     * get the "Version compatible" property value
     *
     * @exception IllegalStateException the object disposed.
     */
    public VersionCompatible getVersion()
        throws IllegalStateException
    {
        CheckDispose();
        return m_Version;
    }

    /**
     * set the "Version compatible" property
     *
     * @param Value new value
     *
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation or the object disposed.
     * @exception IllegalArgumentException the argument Value has unknown 
     * value.
     */
    public void setVersion( VersionCompatible Value )
        throws IllegalStateException, IllegalArgumentException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation" );
        m_Version = Value;
        if( Value == VersionCompatible.ftr_version_compatible )
        {
            m_InternalVersion = FTR_VERSION_COMPATIBLE;
            return;
        }
        if( Value == VersionCompatible.ftr_version_current )
        {
            m_InternalVersion = FTR_VERSION_CURRENT;
            return;
        }
        if( Value == VersionCompatible.ftr_version_previous )
        {
            m_InternalVersion = FTR_VERSION_PREVIOUS;
            return;
        }
        throw new IllegalArgumentException( "The argument Value has unknown value" );
    }

    /**
     * get the "Fast Mode" property value
     *
     * @exception IllegalStateException the object disposed.
     */
    public boolean getFastMode()
        throws IllegalStateException
    {
        CheckDispose();
        return m_bFastMode;
    }

    /**
     * set the "Fast Mode" property value
     * 
     * Set to <code>true</code>, if you want to use fast mode.
     *
     * @param bFastMode new value
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation or the object disposed.
     */
    public void setFastMode( boolean bFastMode )
        throws IllegalStateException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation" );
        m_bFastMode = bFastMode;
    }

    /**
     * Clean all allocated resources
     *
     * Decrements the reference count for the library.
     * If the reference count on the library falls to 0, the SDK library
     * is uninitialized.
     */
    public void Dispose()
    {
        if( m_bDispose )
            return;

        
        if( (m_WorkedThread != null) && m_WorkedThread.isAlive() )
        {
            m_bCancel = true;
            try
            {
                m_WorkedThread.join( 3000 );
                if( m_WorkedThread.isAlive() )
                    m_WorkedThread.interrupt();
                m_WorkedThread = null;
            }
            catch( InterruptedException e )
            {
                m_WorkedThread.interrupt();
                m_WorkedThread = null;
            }
        }

        synchronized ( m_InitLock )
        {
            m_RefCount--;

            if( m_RefCount == 0 )
                FutronicTerminate();
        }

        m_bDispose = true;
    }

    /**
     * State callback function. It's called from native code.
     *
     * @param Progress data capture progress information.
     * @param StateMask a bit mask indicating what arguments are provided.
     * @param Signal this signal should be used to interact with a user.
     * @param BitmapWidth contain a width of the bitmap to be displayed.
     * @param BitmapHeight contain a height of the bitmap to be displayed.
     * @param pBitmap contain a bitmap data.
     *
     * @return user response value
     */
    protected int cbControl( FTR_PROGRESS Progress, int StateMask, int Signal,
                             int BitmapWidth, int BitmapHeight,
                             byte[] pBitmap )
    {
        int nRetCode = FTR_CONTINUE;

        if( (StateMask & FTR_STATE_SIGNAL_PROVIDED) != 0 )
        {
            switch( Signal )
            {
            case FTR_SIGNAL_TOUCH_SENSOR:
                m_CallBack.OnPutOn( Progress );
                break;

            case FTR_SIGNAL_TAKE_OFF:
                m_CallBack.OnTakeOff( Progress );
                break;

            case FTR_SIGNAL_FAKE_SOURCE:
                if( m_CallBack.OnFakeSource( Progress ) )
                    nRetCode = FTR_CANCEL;
                break;

            default:
                assert( false );
                break;
            }
        }

        if( (StateMask & FTR_STATE_FRAME_PROVIDED) != 0 )
        {
            BufferedImage hImage = new BufferedImage( BitmapWidth, 
                                                      BitmapHeight,
                                                      BufferedImage.TYPE_BYTE_GRAY );
            DataBuffer db1 = hImage.getRaster().getDataBuffer();
            for( int i = 0; i < db1.getSize(); i++ )
            {
                db1.setElem( i, pBitmap[i] );
            }

            m_CallBack.UpdateScreenImage( hImage );
        }

        if( m_bCancel )
        {
            nRetCode = FTR_CANCEL;
            m_bCancel = false;
        }

        return nRetCode;
    }


    protected void finalize()
    {
        if( !m_bDispose )
            Dispose();
    }
    
    /**
     * If the class is disposed, this function raises an exception.
     *
     * This function must be called before any operation in all functions.
     *
     * @exception  IllegalStateException The class instance is disposed. Any 
     * calls are prohibited.
     */
    protected void CheckDispose()
        throws IllegalStateException
    {
        if( m_bDispose )
        {
            throw new IllegalStateException( "The object disposed");
        }
    }
        
    /**
     * <code>true</code> if the object disposed explicitly by calling 
     * <code>Dispose</code> method, otherwise <code>false</code>.
     * The default value is <code>false</code>.
     */
    protected boolean m_bDispose;
    
    /**
     * <code>true</code> if the library should activate Live Finger Detection 
     * (LFD) feature. You cannot modify this variable directly. Use the 
     * <code>getFakeDetection</code> and <code>setFakeDetection</code> methods.
     * The default value is <code>false</code>.
     */
    protected boolean m_bFakeDetection;

    /**
     * <code>true</code> if the library should raise the "Fake Detection Event 
     * Handler". You cannot modify this variable directly. Use the 
     * <code>getFFDControl</code> and <code>setFFDControl</code> methods.
     * The default value is <code>true</code>.
     */
    protected boolean m_bFFDControl;

    /**
     * <code>true</code> if the library should abort current process. You cann't
     * modify this variable directly. Use the <code>OnCancel</code> method.
     * The default value is <code>true</code>.
     */
    protected boolean m_bCancel;

    /**
     * Current False Accepting Ratio value. Contains only one of
     * predefined values.
     * The default value is <code>FarnValues.farn_normal</code>.
     */
    protected FarnValues  m_FarnLevel;

    /**
     * The default value is <code>VersionCompatible.ftr_version_current</code>.
     */
    protected VersionCompatible  m_Version;

    /**
     * The default value is <code>VersionCompatible.ftr_version_current</code>.
     */
    protected int  m_InternalVersion;

    /**
     * Current False Accepting Ratio value. It may contains any valid
     * value.
     */
    protected int m_FARN;

    /**
     * Fast mode property
     * Set this property to  <code>true</code> to use fast mode. You cannot modify this variable directly. Use the 
     * <code>getFastMode</code> and <code>setFastMode</code> methods.
     * The default value is <code>false</code>.
     */
    protected boolean m_bFastMode;

    /**
     * Current frame source.
     */
    protected final int m_FrameSource = FSD_FUTRONIC_USB;

    /**
     * Reference to the operation thread: capture, enrollment etc.
     */
    protected Thread m_WorkedThread;
    
    protected ICallBack m_CallBack;

    /**
     * Current state for the class.
     */
    protected EnrollmentState m_State;

    ///////////////////////////////////////////////////////////////////////////
    // Native API of ftrJSDKHelper library
    ///////////////////////////////////////////////////////////////////////////
 
    /**
     * Activates the Futronic SDK interface.
     */
    protected native int FutronicInitialize();
    
    /**
     * Deactivates the Futronic API.
     */
    protected native void FutronicTerminate();
    
    /**
     * Creates the fingerprint template for the desired purpose
     *
     * Function set parameters specific fro enrollment operation and does enrollment.
     */
    protected native int FutronicEnroll();
    
    /**
     * Creates the fingerprint template for the desired purpose
     *
     * Function set parameters specific fro enrollment operation and does enrollment.
     */
    protected native int VerificationProcess();

    /**
     * The native function does of the enrollment operation for the identification purpose.
     *
     * Function set parameters specific fro enrollment operation and does enrollment
     * for the identification purpose.
     */
    protected native int GetBaseTemplateProcess();
    
    /**
     * The native function sets parameters for the identification purpose and 
     * does identification.
     *
     * @param rgTemplates the set of source templates.
     * @param Result If the function succeeds, field <code>m_Index</code> contains an 
     * index of the matched record (the first element has an index 0) or -1, if
     * no matching source templates are detected.
     *
     * @return the Futronic SDK return code.
     */
    protected native int IdentifyProcess( FtrIdentifyRecord[] rgTemplates, FtrIdentifyResult Result );

    /**
     * The native function gets a value that indicates whether a library is trial version.
     * 
     * @return <c>true<c> if this is a trial version otherwise <c>false<c>
     */
    protected native boolean FutronicIsTrial();
    
    /**
     * The native function gets a value that specify identification limit value.
     * 
     * @return identification limit value. If property contains Integer.MAX_VALUE 
     * that is "no limits"
     */
    protected native int FutronicIdentificationsLeft();
}