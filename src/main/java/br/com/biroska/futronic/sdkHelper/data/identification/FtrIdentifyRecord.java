package br.com.biroska.futronic.sdkHelper.data.identification;

/**
 * Identification information record.
 * @author Shustikov
 */
public class FtrIdentifyRecord
{
    
    /** Creates a new instance of FtrIdentifyRecord */
    public FtrIdentifyRecord()
    {
        m_KeyValue = null;
        m_Template = null;
    }
    
    /**
     * The current record unique ID.
     * This record should be set from the main program.
     * The maximum unique ID length is 16 bytes.
     */
    public byte[]   m_KeyValue;
    
    /**
     * The current template.
     */
    public byte[]   m_Template;
}
