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
import type { IoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO } from './IoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO';
import {
    IoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVOFromJSON,
    IoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVOFromJSONTyped,
    IoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVOToJSON,
} from './IoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO';

/**
 * 
 * @export
 * @interface IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO
 */
export interface IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO {
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO
     */
    code: number;
    /**
     * 
     * @type {IoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO}
     * @memberof IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO
     */
    data?: IoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO | null;
    /**
     * 
     * @type {boolean}
     * @memberof IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO
     */
    isSuccessful: boolean;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO
     */
    message: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO interface.
 */
export function instanceOfIoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO(value: object): value is IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO {
    if (!('code' in value) || value['code'] === undefined) return false;
    if (!('isSuccessful' in value) || value['isSuccessful'] === undefined) return false;
    if (!('message' in value) || value['message'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVOFromJSON(json: any): IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO {
    return IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO {
    if (json == null) {
        return json;
    }
    return {
        
        'code': json['code'],
        'data': json['data'] == null ? undefined : IoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVOFromJSON(json['data']),
        'isSuccessful': json['isSuccessful'],
        'message': json['message'],
    };
}

export function IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVOToJSON(value?: IoSakurasouControllerVoCommonResponseIoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'code': value['code'],
        'data': IoSakurasouControllerVoPageResultIoSakurasouControllerVoImagePageVOToJSON(value['data']),
        'isSuccessful': value['isSuccessful'],
        'message': value['message'],
    };
}

