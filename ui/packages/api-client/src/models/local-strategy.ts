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
import type { StrategyTypeEnum } from './strategy-type-enum';

/**
 * 
 * @export
 * @interface LocalStrategy
 */
export interface LocalStrategy {
    /**
     * 
     * @type {string}
     * @memberof LocalStrategy
     */
    'thumbnailFolder': string;
    /**
     * 
     * @type {string}
     * @memberof LocalStrategy
     */
    'uploadFolder': string;
    /**
     * 
     * @type {StrategyTypeEnum}
     * @memberof LocalStrategy
     */
    'strategyType': StrategyTypeEnum;
}



