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
import type { HoshizoraStatusVO } from './hoshizora-status-vo';
// May contain unused imports in some cases
// @ts-ignore
import type { SystemStatusVO } from './system-status-vo';

/**
 * 
 * @export
 * @interface SystemOverviewVO
 */
export interface SystemOverviewVO {
    /**
     * 
     * @type {HoshizoraStatusVO}
     * @memberof SystemOverviewVO
     */
    'hoshizoraStatus': HoshizoraStatusVO;
    /**
     * 
     * @type {SystemStatusVO}
     * @memberof SystemOverviewVO
     */
    'systemStatus': SystemStatusVO;
}
