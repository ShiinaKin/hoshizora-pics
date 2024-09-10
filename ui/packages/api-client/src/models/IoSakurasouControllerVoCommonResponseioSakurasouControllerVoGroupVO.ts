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
import type { IoSakurasouControllerVoGroupVO } from './IoSakurasouControllerVoGroupVO';
import {
    IoSakurasouControllerVoGroupVOFromJSON,
    IoSakurasouControllerVoGroupVOFromJSONTyped,
    IoSakurasouControllerVoGroupVOToJSON,
} from './IoSakurasouControllerVoGroupVO';

/**
 * 
 * @export
 * @interface IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO
 */
export interface IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO {
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO
     */
    code: number;
    /**
     * 
     * @type {IoSakurasouControllerVoGroupVO}
     * @memberof IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO
     */
    data?: IoSakurasouControllerVoGroupVO;
    /**
     * 
     * @type {boolean}
     * @memberof IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO
     */
    isSuccessful: boolean;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO
     */
    message: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO interface.
 */
export function instanceOfIoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO(value: object): value is IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO {
    if (!('code' in value) || value['code'] === undefined) return false;
    if (!('isSuccessful' in value) || value['isSuccessful'] === undefined) return false;
    if (!('message' in value) || value['message'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVOFromJSON(json: any): IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO {
    return IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO {
    if (json == null) {
        return json;
    }
    return {
        
        'code': json['code'],
        'data': json['data'] == null ? undefined : IoSakurasouControllerVoGroupVOFromJSON(json['data']),
        'isSuccessful': json['isSuccessful'],
        'message': json['message'],
    };
}

export function IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVOToJSON(value?: IoSakurasouControllerVoCommonResponseioSakurasouControllerVoGroupVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'code': value['code'],
        'data': IoSakurasouControllerVoGroupVOToJSON(value['data']),
        'isSuccessful': value['isSuccessful'],
        'message': value['message'],
    };
}

