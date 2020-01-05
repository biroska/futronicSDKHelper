package br.com.biroska.futronic.sdkHelper.data.proccess;

/**
 * Data capture progress information.
 */
public class FTR_PROGRESS
{
    /** Creates a new instance of FTR_PROGRESS */
    public FTR_PROGRESS()
    {
    }

    /**
     * Currently requested frame number.
     */
    public int m_Count;
    
    /**
     * Flag indicating whether the frame is requested not the first time.
     */
    public boolean m_bIsRepeated;
    
    /**
     * Total number of frames to be captured.
     */
    public int m_Total;
}
