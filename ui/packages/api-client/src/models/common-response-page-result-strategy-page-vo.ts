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
import type { PageResultStrategyPageVO } from './page-result-strategy-page-vo';

/**
 * 
 * @export
 * @interface CommonResponsePageResultStrategyPageVO
 */
export interface CommonResponsePageResultStrategyPageVO {
    /**
     * 
     * @type {number}
     * @memberof CommonResponsePageResultStrategyPageVO
     */
    'code': number;
    /**
     * 
     * @type {PageResultStrategyPageVO}
     * @memberof CommonResponsePageResultStrategyPageVO
     */
    'data'?: PageResultStrategyPageVO | null;
    /**
     * 
     * @type {boolean}
     * @memberof CommonResponsePageResultStrategyPageVO
     */
    'isSuccessful': boolean;
    /**
     * 
     * @type {string}
     * @memberof CommonResponsePageResultStrategyPageVO
     */
    'message': string;
}

