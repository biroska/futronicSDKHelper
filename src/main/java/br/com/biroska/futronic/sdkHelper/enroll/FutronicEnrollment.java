package br.com.biroska.futronic.sdkHelper.enroll;

import br.com.biroska.futronic.sdkHelper.base.FutronicSdkBase;
import br.com.biroska.futronic.sdkHelper.enums.EnrollmentState;
import br.com.biroska.futronic.sdkHelper.exception.FutronicException;

/**
 * The "Enrollment operation" class
 */
public class FutronicEnrollment extends FutronicSdkBase implements Runnable
{
    protected static int MinModelsValue = 1;
    protected static int MaxModelsValue = 10;
    protected static int DefaultModelsValue = 5;
    
    /**
     * The FutronicEnrollment class constructor.
     * Initialize a new instance of the FutronicEnrollment class.
     *
     * @exception FutronicException Error occurs during SDK initialization. 
     * To get error code, see method <code>getErrorCode</code> of FutronicException
     * class.
     */
    public FutronicEnrollment()
        throws FutronicException
    {
        super();
        m_bMIOTControlOff = true;
        m_Template = null;
        m_Quality = 0;
        m_MaxModels = DefaultModelsValue;
    }
    
    /**
     * This function starts the enrollment operation.
     *
     * The enrollment operation starts in its own thread. To interact with the 
     * enrollment operation caller must implement the <code>IEnrollmentCallBack</code>
     * interface and should specify it. The interface methods denote following:
     * <table>
     * <thead>
     *  <tr>
     *      <td>Method</td>
     *      <td>Description</td>
     *  </tr>
     * </thead>
     * <tr>
     *      <td>OnPutOn</td>
     *      <td>Invitation for touching the fingerprint scanner surface.</td>
     *  </tr>
     * <tr>
     *      <td>OnTakeOff</td>
     *      <td>Proposal to take off a finger from the scanner surface.</td>
     *  </tr>
     * <tr>
     *      <td>UpdateScreenImage</td>
     *      <td>The "Show the current fingerprint image" event.</td>
     *  </tr>
     * <tr>
     *      <td>OnFakeSource</td>
     *      <td>The "Fake Finger Detected"  event. This event raises only if 
     *      <code>FakeDetection</code> and <code>FFDControl</code> properties are 
     *      <code>true</code>.</td>
     *  </tr>
     * <tr>
     *      <td>OnEnrollmentComplete</td>
     *      <td>This event is signaled when the enrollment operation is completed.
     *      If the operation is completed successfully, you may get a template.</td>
     *  </tr>
     * </table>
     * If the enrollment operation is completed successfully, you may get a 
     * template. The next call of the enrollment operation removes the last 
     * created template.
     *
     * @param callBack reference to call back interface (can not be NULL)
     *
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation or the object disposed.
     *
     * @exception NullPointerException a null reference parameter callBack is 
     * passed to the function.
     */
    public void Enrollment( IEnrollmentCallBack callBack )
        throws IllegalStateException, NullPointerException
    {
        CheckDispose();

        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation" );

        if( callBack == null )
            throw new NullPointerException( "A null reference parameter callBack is passed to the function." );

        m_State = EnrollmentState.process_in_progress;
        m_CallBack = callBack;
        m_bCancel = false;
        
        // run new thread
        m_WorkedThread = new Thread( this, "Enrollment operation" );
        m_WorkedThread.start();
    }
        
    /**
     * get the MIOT mode setting
     *
     * @exception IllegalStateException the object disposed.
     */
    public boolean getMIOTControlOff()
        throws IllegalStateException
    {
        CheckDispose();
        return m_bMIOTControlOff;
    }

    /**
     * Enable or disable the MIOT mode
     *
     * Set to <code>true</code>, if you want to enable the MIOT mode.
     *
     * @param bMIOTControl new value
     *
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation or the object disposed.
     */
    public void setMIOTControlOff( boolean bMIOTControlOff )
        throws IllegalStateException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation" );
        m_bMIOTControlOff = bMIOTControlOff;
    }

    /**
     * get max number of models in one template.
     *
     * @exception IllegalStateException the object disposed.
     */
    public int getMaxModels()
        throws IllegalStateException
    {
        CheckDispose();
        return m_MaxModels;
    }

    /**
     * Set max number of models in one template.
     *
     * This value must be between 3 and 10.
     *
     * @param MaxModels new value
     *
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation.
     * @exception IllegalStateException the object disposed.
     * @exception IllegalArgumentException a method has been passed an 
     * inappropriate argument MaxModels.
     */
    public void setMaxModels( int MaxModels )
        throws IllegalStateException, IllegalArgumentException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation" );

        if( MaxModels < 1 || MaxModels > 10 )
            throw new IllegalArgumentException( "The value of argument 'MaxModels' is outside the allowable range of values.");

        m_MaxModels = MaxModels;
    }
        
    /**
     * Returns the template of the last enrollment operation.
     *
     * Returns a copy of template. If the last enrollment operation is 
     * unsuccessful, the return code is null.
     *
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation. The enrollment operation is started. 
     * @exception IllegalStateException the object disposed.
     */
    public byte[] getTemplate()
        throws IllegalStateException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation. The enrollment operation is started" );
        if( m_Template == null )
            return null;
        return m_Template.clone();
    }
        
    /**
     * Return the quality of the template.
     *
     * Return value may be one of the following: 1 (the lowest quality) to  10 
     * (best quality). If the enrollment operation is unsuccessful or was not 
     * started, the return value is 0.
     *
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation. The enrollment operation is started. 
     * @exception IllegalStateException the object disposed.
     */
    public int getQuality()
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation. The enrollment operation is started" );
        return m_Quality;
    }
    
    /**
     * The main thread of the enrollment operation.
     */
    public void run()
    {
        int nResult = RETCODE_INTERNAL_ERROR;
        try
        {
            synchronized( m_SyncRoot )
            {
                m_Template = null;
                m_Quality = 0;
                nResult = FutronicEnroll();
            }
        }
        finally
        {
            m_State = EnrollmentState.ready_to_process;
            ((IEnrollmentCallBack)m_CallBack).OnEnrollmentComplete( nResult == RETCODE_OK, nResult );
        }
    }
        
    /**
     * The MIOT mode setting.
     * You cannot modify this variable directly. Use the <code>getMIOTControl</code>
     * and <code>setMIOTControl</code> methods.
     * The default value is <code>false</code>.
     */
    private boolean     m_bMIOTControlOff;

    /**
     * The template of the last enrollment operation.
     * You cannot modify this variable directly. Use the <code>getTemplate</code> method.
     */
    private byte[]      m_Template;

    /**
     * Estimation of a template quality in terms of recognition:
     * 1 corresponds to the worst quality, 10 denotes the best.
     */
    private int         m_Quality;

    /**
     * Max number of models in one template. This value must be between 3 and 10.
     */
    private int         m_MaxModels;

}
