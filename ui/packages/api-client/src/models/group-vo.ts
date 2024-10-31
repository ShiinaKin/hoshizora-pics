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
import type { GroupConfig } from './group-config';

/**
 * 
 * @export
 * @interface GroupVO
 */
export interface GroupVO {
    /**
     * 
     * @type {string}
     * @memberof GroupVO
     */
    'description'?: string | null;
    /**
     * 
     * @type {GroupConfig}
     * @memberof GroupVO
     */
    'groupConfig': GroupConfig;
    /**
     * 
     * @type {number}
     * @memberof GroupVO
     */
    'id': number;
    /**
     * 
     * @type {string}
     * @memberof GroupVO
     */
    'name': string;
    /**
     * 
     * @type {Array<string>}
     * @memberof GroupVO
     */
    'roles': Array<string>;
    /**
     * 
     * @type {number}
     * @memberof GroupVO
     */
    'strategyId': number;
}
