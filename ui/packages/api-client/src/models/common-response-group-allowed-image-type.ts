/* tslint:disable */
/* eslint-disable */
/**
 * HoshizoraPics API
 * API for testing and demonstration purposes.
 *
 * The version of the OpenAPI document: latest
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


// May contain unused imports in some cases
// @ts-ignore
import type { GroupAllowedImageType } from './group-allowed-image-type';

/**
 * 
 * @export
 * @interface CommonResponseGroupAllowedImageType
 */
export interface CommonResponseGroupAllowedImageType {
    /**
     * 
     * @type {number}
     * @memberof CommonResponseGroupAllowedImageType
     */
    'code': number;
    /**
     * 
     * @type {GroupAllowedImageType}
     * @memberof CommonResponseGroupAllowedImageType
     */
    'data'?: GroupAllowedImageType | null;
    /**
     * 
     * @type {boolean}
     * @memberof CommonResponseGroupAllowedImageType
     */
    'isSuccessful': boolean;
    /**
     * 
     * @type {string}
     * @memberof CommonResponseGroupAllowedImageType
     */
    'message': string;
}
