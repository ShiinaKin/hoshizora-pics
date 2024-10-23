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
import type { IoSakurasouControllerVoUserPageVO } from './IoSakurasouControllerVoUserPageVO';
import {
    IoSakurasouControllerVoUserPageVOFromJSON,
    IoSakurasouControllerVoUserPageVOFromJSONTyped,
    IoSakurasouControllerVoUserPageVOToJSON,
} from './IoSakurasouControllerVoUserPageVO';

/**
 * 
 * @export
 * @interface IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO
 */
export interface IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO {
    /**
     * 
     * @type {Array<IoSakurasouControllerVoUserPageVO>}
     * @memberof IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO
     */
    data: Array<IoSakurasouControllerVoUserPageVO>;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO
     */
    page: number;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO
     */
    pageSize: number;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO
     */
    total: number;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO
     */
    totalPage: number;
}

/**
 * Check if a given object implements the IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO interface.
 */
export function instanceOfIoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO(value: object): value is IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO {
    if (!('data' in value) || value['data'] === undefined) return false;
    if (!('page' in value) || value['page'] === undefined) return false;
    if (!('pageSize' in value) || value['pageSize'] === undefined) return false;
    if (!('total' in value) || value['total'] === undefined) return false;
    if (!('totalPage' in value) || value['totalPage'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVOFromJSON(json: any): IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO {
    return IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO {
    if (json == null) {
        return json;
    }
    return {
        
        'data': ((json['data'] as Array<any>).map(IoSakurasouControllerVoUserPageVOFromJSON)),
        'page': json['page'],
        'pageSize': json['pageSize'],
        'total': json['total'],
        'totalPage': json['totalPage'],
    };
}

export function IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVOToJSON(value?: IoSakurasouControllerVoPageResultIoSakurasouControllerVoUserPageVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'data': ((value['data'] as Array<any>).map(IoSakurasouControllerVoUserPageVOToJSON)),
        'page': value['page'],
        'pageSize': value['pageSize'],
        'total': value['total'],
        'totalPage': value['totalPage'],
    };
}

