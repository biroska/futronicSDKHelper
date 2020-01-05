package br.com.biroska.futronic.sdkHelper.enums;

/**
 * Contains some predefined levels for FAR (False Accepting Ratio)
 */
public enum FarnValues
{
    farn_low,
    farn_below_normal,
    farn_normal,
    farn_above_normal,
    farn_high,
    farn_max,
    /**
     * This value cannot be used as FARnLevel parameter.
     * The farn_custom shows that a custom value is assigned for FAR.
     */
    farn_custom
}
