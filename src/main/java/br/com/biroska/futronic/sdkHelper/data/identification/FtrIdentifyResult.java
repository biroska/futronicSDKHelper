package br.com.biroska.futronic.sdkHelper.data.identification;

/**
 * The class contains identification result
 * @author Shustikov
 */
public class FtrIdentifyResult
{
    /** Creates a new instance of FtrIdentifyResult */
    public FtrIdentifyResult()
    {
        m_Index = -1;
    }

    /**
     * If the identification process succeeds, field contains an index of the 
     * matched record (the first element has an index 0) or -1, if
     * no matching source templates are detected.
     */
    public int m_Index;
}
