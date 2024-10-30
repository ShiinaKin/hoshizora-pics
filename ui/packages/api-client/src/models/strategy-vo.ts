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
import type { KotlinxDatetimeLocalDateTime } from './kotlinx-datetime-local-date-time';
// May contain unused imports in some cases
// @ts-ignore
import type { StrategyConfigSealed } from './strategy-config-sealed';
// May contain unused imports in some cases
// @ts-ignore
import type { StrategyTypeEnum } from './strategy-type-enum';

/**
 * 
 * @export
 * @interface StrategyVO
 */
export interface StrategyVO {
    /**
     * 
     * @type {StrategyConfigSealed}
     * @memberof StrategyVO
     */
    'config': StrategyConfigSealed;
    /**
     * 
     * @type {KotlinxDatetimeLocalDateTime}
     * @memberof StrategyVO
     */
    'createTime': KotlinxDatetimeLocalDateTime;
    /**
     * 
     * @type {number}
     * @memberof StrategyVO
     */
    'id': number;
    /**
     * 
     * @type {string}
     * @memberof StrategyVO
     */
    'name': string;
    /**
     * 
     * @type {StrategyTypeEnum}
     * @memberof StrategyVO
     */
    'type': StrategyTypeEnum;
}



