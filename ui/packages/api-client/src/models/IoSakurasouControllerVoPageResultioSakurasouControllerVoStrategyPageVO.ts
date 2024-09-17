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

import { mapValues } from '../runtime';
import type { IoSakurasouControllerVoStrategyPageVO } from './IoSakurasouControllerVoStrategyPageVO';
import {
    IoSakurasouControllerVoStrategyPageVOFromJSON,
    IoSakurasouControllerVoStrategyPageVOFromJSONTyped,
    IoSakurasouControllerVoStrategyPageVOToJSON,
} from './IoSakurasouControllerVoStrategyPageVO';

/**
 * 
 * @export
 * @interface IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO
 */
export interface IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO {
    /**
     * 
     * @type {Array<IoSakurasouControllerVoStrategyPageVO>}
     * @memberof IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO
     */
    list: Array<IoSakurasouControllerVoStrategyPageVO>;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO
     */
    page: number;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO
     */
    pageSize: number;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO
     */
    total: number;
}

/**
 * Check if a given object implements the IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO interface.
 */
export function instanceOfIoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO(value: object): value is IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO {
    if (!('list' in value) || value['list'] === undefined) return false;
    if (!('page' in value) || value['page'] === undefined) return false;
    if (!('pageSize' in value) || value['pageSize'] === undefined) return false;
    if (!('total' in value) || value['total'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVOFromJSON(json: any): IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO {
    return IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO {
    if (json == null) {
        return json;
    }
    return {
        
        'list': ((json['list'] as Array<any>).map(IoSakurasouControllerVoStrategyPageVOFromJSON)),
        'page': json['page'],
        'pageSize': json['pageSize'],
        'total': json['total'],
    };
}

export function IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVOToJSON(value?: IoSakurasouControllerVoPageResultioSakurasouControllerVoStrategyPageVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'list': ((value['list'] as Array<any>).map(IoSakurasouControllerVoStrategyPageVOToJSON)),
        'page': value['page'],
        'pageSize': value['pageSize'],
        'total': value['total'],
    };
}

