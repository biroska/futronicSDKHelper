package br.com.biroska.futronic.sdkHelper.enums;

/**
 * The process states constants
 */
public enum EnrollmentState
{
    /**
     * The "ready to enrollment" state. class is ready to receive a base 
     * template and start the identification operation
     */
    ready_to_process,

    /**
     * Class is receiving the base template or the enrollment operation is 
     * starting
     */
    process_in_progress,

    /**
     * Class is ready to start the identification operation
     */
    ready_to_continue,

    /**
     * The identification process is starting for this class
     */
    continue_in_progress
    
}
