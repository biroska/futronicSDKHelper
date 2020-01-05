package br.com.biroska.futronic.sdkHelper.data.verification;

import br.com.biroska.futronic.sdkHelper.base.FutronicSdkBase;
import br.com.biroska.futronic.sdkHelper.enums.EnrollmentState;
import br.com.biroska.futronic.sdkHelper.exception.FutronicException;

/**
 * The FutronicVerification class captures an image from the attached
 * scanner, builds the corresponding template and compares it with the source
 * template.
 *
 * @author Shustikov
 */
public class FutronicVerification extends FutronicSdkBase implements Runnable
{
    
    /** 
     * The FutronicVerification class constructor. Initialize a new instance of 
     * the FutronicVerification class.
     *
     * @param Template a source template for verification.
     *
     * @exception FutronicException error occurs during SDK initialization. To 
     * get error code, see property ErrorCode of the FutronicException class.
     * @exception NullPointerException a null reference parameter Template is 
     * passed to the constructor.
     */
    public FutronicVerification( byte[] Template )
        throws FutronicException, NullPointerException
    {
        super();
        if( Template == null )
            throw new NullPointerException( "A null reference parameter Template is passed to the constructor." );
        m_Template = Template.clone();
        m_FARNValue = 1;
        m_bResult = false;
    }
    
    /**
     * This function starts the verification operation. 
     *
     * The verification operation starts in its own thread. To interact with 
     * the verification operation caller must implement the <code>IVerificationCallBack</code>
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
     *      <td>The "Fake Finger Detected" event. This event raises only if 
     *      <code>FakeDetection</code> and <code>FFDControl</code> properties are 
     *      <code>true</code>.</td>
     *  </tr>
     * <tr>
     *      <td>OnVerificationComplete</td>
     *      <td>This event is signaled when the verification operation is completed.</td>
     *  </tr>
     * </table>
     *
     * @param callBack reference to call back interface (can not be NULL)
     *
     * @exception IllegalStateException the class instance is disposed. Any 
     * calls are prohibited.
     *
     * @exception IllegalStateException the object is not in an appropriate 
     * state for the requested operation or the object disposed.
     *
     * @exception NullPointerException a null reference parameter callBack is 
     * passed to the function.
     */
    public void Verification( IVerificationCallBack callBack )
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
        m_WorkedThread = new Thread( this, "Verification operation" );
        m_WorkedThread.start();
    }

    /**
     * The last verification result.
     *
     * @exception IllegalStateException the class instance is disposed. Any 
     * calls are prohibited.
     * @exception IllegalStateException the object is not in an appropriate state 
     * for the requested operation. The verification operation is not finished.
     */
    public boolean getResult()
        throws IllegalStateException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation." +
                                             "The verification operation is not finished." );
        return m_bResult;
    }

    /**
     * The FARN value returned during the last verification operation.
     *
     * @exception IllegalStateException the class instance is disposed. Any 
     * calls are prohibited.
     * @exception IllegalStateException the object is not in an appropriate state 
     * for the requested operation. The verification operation is not finished.
     */
    public int getFARNValue()
        throws IllegalStateException
    {
        CheckDispose();
        if( m_State != EnrollmentState.ready_to_process )
            throw new IllegalStateException( "The object is not in an appropriate state for the requested operation." +
                                             "The verification operation is not finished." );
        return m_FARNValue;
    }

    /**
     * The main thread of the verification operation.
     * Function prepares all necessary parameters for the verification
     * operation and calls the function from unmanaged code. This native 
     * function sets all parameters for SDK and starts the verification 
     * operation.
     */
    public void run()
    {
        int nResult = RETCODE_INTERNAL_ERROR;

        try
        {
            synchronized( m_SyncRoot )
            {
                m_bResult = false;
                nResult = VerificationProcess();
            }
        }
        finally
        {
            m_State = EnrollmentState.ready_to_process;

            ((IVerificationCallBack)m_CallBack).OnVerificationComplete( nResult == RETCODE_OK, nResult, m_bResult );
        }
    }

    /**
     * This is a copy of the source template.
     */
    private byte[]      m_Template;

    /**
     * The last verification result.
     * You cannot access to this variable directly. Use the Result property.
     */
    private boolean     m_bResult;

    /**
     * The FARN value returned during the last verification operation.
     * You cannot access to this variable directly. Use the FARNValue property.
     */
    private int         m_FARNValue;
}
