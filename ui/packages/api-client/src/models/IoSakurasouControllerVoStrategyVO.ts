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
import type { IoSakurasouModelStrategyStrategyConfig } from './IoSakurasouModelStrategyStrategyConfig';
import {
    IoSakurasouModelStrategyStrategyConfigFromJSON,
    IoSakurasouModelStrategyStrategyConfigFromJSONTyped,
    IoSakurasouModelStrategyStrategyConfigToJSON,
} from './IoSakurasouModelStrategyStrategyConfig';
import type { IoSakurasouModelStrategyStrategyType } from './IoSakurasouModelStrategyStrategyType';
import {
    IoSakurasouModelStrategyStrategyTypeFromJSON,
    IoSakurasouModelStrategyStrategyTypeFromJSONTyped,
    IoSakurasouModelStrategyStrategyTypeToJSON,
} from './IoSakurasouModelStrategyStrategyType';

/**
 * 
 * @export
 * @interface IoSakurasouControllerVoStrategyVO
 */
export interface IoSakurasouControllerVoStrategyVO {
    /**
     * 
     * @type {IoSakurasouModelStrategyStrategyConfig}
     * @memberof IoSakurasouControllerVoStrategyVO
     */
    config: IoSakurasouModelStrategyStrategyConfig;
    /**
     * 
     * @type {KotlinxDatetimeLocalDateTime}
     * @memberof IoSakurasouControllerVoStrategyVO
     */
    createTime: KotlinxDatetimeLocalDateTime;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoStrategyVO
     */
    id: number;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerVoStrategyVO
     */
    name: string;
    /**
     * 
     * @type {IoSakurasouModelStrategyStrategyType}
     * @memberof IoSakurasouControllerVoStrategyVO
     */
    type: IoSakurasouModelStrategyStrategyType;
}



/**
 * Check if a given object implements the IoSakurasouControllerVoStrategyVO interface.
 */
export function instanceOfIoSakurasouControllerVoStrategyVO(value: object): value is IoSakurasouControllerVoStrategyVO {
    if (!('config' in value) || value['config'] === undefined) return false;
    if (!('createTime' in value) || value['createTime'] === undefined) return false;
    if (!('id' in value) || value['id'] === undefined) return false;
    if (!('name' in value) || value['name'] === undefined) return false;
    if (!('type' in value) || value['type'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoStrategyVOFromJSON(json: any): IoSakurasouControllerVoStrategyVO {
    return IoSakurasouControllerVoStrategyVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoStrategyVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoStrategyVO {
    if (json == null) {
        return json;
    }
    return {
        
        'config': IoSakurasouModelStrategyStrategyConfigFromJSON(json['config']),
        'createTime': KotlinxDatetimeLocalDateTimeFromJSON(json['createTime']),
        'id': json['id'],
        'name': json['name'],
        'type': IoSakurasouModelStrategyStrategyTypeFromJSON(json['type']),
    };
}

export function IoSakurasouControllerVoStrategyVOToJSON(value?: IoSakurasouControllerVoStrategyVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'config': IoSakurasouModelStrategyStrategyConfigToJSON(value['config']),
        'createTime': KotlinxDatetimeLocalDateTimeToJSON(value['createTime']),
        'id': value['id'],
        'name': value['name'],
        'type': IoSakurasouModelStrategyStrategyTypeToJSON(value['type']),
    };
}

