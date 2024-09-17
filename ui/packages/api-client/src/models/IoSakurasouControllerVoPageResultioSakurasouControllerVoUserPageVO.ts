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
 * @interface IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO
 */
export interface IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO {
    /**
     * 
     * @type {Array<IoSakurasouControllerVoUserPageVO>}
     * @memberof IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO
     */
    list: Array<IoSakurasouControllerVoUserPageVO>;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO
     */
    page: number;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO
     */
    pageSize: number;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO
     */
    total: number;
}

/**
 * Check if a given object implements the IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO interface.
 */
export function instanceOfIoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO(value: object): value is IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO {
    if (!('list' in value) || value['list'] === undefined) return false;
    if (!('page' in value) || value['page'] === undefined) return false;
    if (!('pageSize' in value) || value['pageSize'] === undefined) return false;
    if (!('total' in value) || value['total'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVOFromJSON(json: any): IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO {
    return IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO {
    if (json == null) {
        return json;
    }
    return {
        
        'list': ((json['list'] as Array<any>).map(IoSakurasouControllerVoUserPageVOFromJSON)),
        'page': json['page'],
        'pageSize': json['pageSize'],
        'total': json['total'],
    };
}

export function IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVOToJSON(value?: IoSakurasouControllerVoPageResultioSakurasouControllerVoUserPageVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'list': ((value['list'] as Array<any>).map(IoSakurasouControllerVoUserPageVOToJSON)),
        'page': value['page'],
        'pageSize': value['pageSize'],
        'total': value['total'],
    };
}

