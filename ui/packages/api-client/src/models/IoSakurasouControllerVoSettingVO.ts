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
import type { KotlinxDatetimeLocalDateTime } from './KotlinxDatetimeLocalDateTime';
import {
    KotlinxDatetimeLocalDateTimeFromJSON,
    KotlinxDatetimeLocalDateTimeFromJSONTyped,
    KotlinxDatetimeLocalDateTimeToJSON,
} from './KotlinxDatetimeLocalDateTime';
import type { IoSakurasouModelSettingSettingConfig } from './IoSakurasouModelSettingSettingConfig';
import {
    IoSakurasouModelSettingSettingConfigFromJSON,
    IoSakurasouModelSettingSettingConfigFromJSONTyped,
    IoSakurasouModelSettingSettingConfigToJSON,
} from './IoSakurasouModelSettingSettingConfig';

/**
 * 
 * @export
 * @interface IoSakurasouControllerVoSettingVO
 */
export interface IoSakurasouControllerVoSettingVO {
    /**
     * 
     * @type {IoSakurasouModelSettingSettingConfig}
     * @memberof IoSakurasouControllerVoSettingVO
     */
    config: IoSakurasouModelSettingSettingConfig;
    /**
     * 
     * @type {KotlinxDatetimeLocalDateTime}
     * @memberof IoSakurasouControllerVoSettingVO
     */
    lastUpdateTime: KotlinxDatetimeLocalDateTime;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerVoSettingVO
     */
    name: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerVoSettingVO interface.
 */
export function instanceOfIoSakurasouControllerVoSettingVO(value: object): value is IoSakurasouControllerVoSettingVO {
    if (!('config' in value) || value['config'] === undefined) return false;
    if (!('lastUpdateTime' in value) || value['lastUpdateTime'] === undefined) return false;
    if (!('name' in value) || value['name'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoSettingVOFromJSON(json: any): IoSakurasouControllerVoSettingVO {
    return IoSakurasouControllerVoSettingVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoSettingVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoSettingVO {
    if (json == null) {
        return json;
    }
    return {
        
        'config': IoSakurasouModelSettingSettingConfigFromJSON(json['config']),
        'lastUpdateTime': KotlinxDatetimeLocalDateTimeFromJSON(json['lastUpdateTime']),
        'name': json['name'],
    };
}

export function IoSakurasouControllerVoSettingVOToJSON(value?: IoSakurasouControllerVoSettingVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'config': IoSakurasouModelSettingSettingConfigToJSON(value['config']),
        'lastUpdateTime': KotlinxDatetimeLocalDateTimeToJSON(value['lastUpdateTime']),
        'name': value['name'],
    };
}
