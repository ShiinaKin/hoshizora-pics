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
import type { IoSakurasouControllerVoSettingVO } from './IoSakurasouControllerVoSettingVO';
import {
    IoSakurasouControllerVoSettingVOFromJSON,
    IoSakurasouControllerVoSettingVOFromJSONTyped,
    IoSakurasouControllerVoSettingVOToJSON,
} from './IoSakurasouControllerVoSettingVO';

/**
 * 
 * @export
 * @interface IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO
 */
export interface IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO {
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO
     */
    code: number;
    /**
     * 
     * @type {{ [key: string]: IoSakurasouControllerVoSettingVO; }}
     * @memberof IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO
     */
    data?: { [key: string]: IoSakurasouControllerVoSettingVO; } | null;
    /**
     * 
     * @type {boolean}
     * @memberof IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO
     */
    isSuccessful: boolean;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO
     */
    message: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO interface.
 */
export function instanceOfIoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO(value: object): value is IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO {
    if (!('code' in value) || value['code'] === undefined) return false;
    if (!('isSuccessful' in value) || value['isSuccessful'] === undefined) return false;
    if (!('message' in value) || value['message'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVOFromJSON(json: any): IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO {
    return IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO {
    if (json == null) {
        return json;
    }
    return {
        
        'code': json['code'],
        'data': json['data'] == null ? undefined : { [key: string]: IoSakurasouControllerVoSettingVO; }FromJSON(json['data']),
        'isSuccessful': json['isSuccessful'],
        'message': json['message'],
    };
}

export function IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVOToJSON(value?: IoSakurasouControllerVoCommonResponseKotlinCollectionsMapKotlinStringIoSakurasouControllerVoSettingVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'code': value['code'],
        'data': { [key: string]: IoSakurasouControllerVoSettingVO; }ToJSON(value['data']),
        'isSuccessful': value['isSuccessful'],
        'message': value['message'],
    };
}
