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
import type { IoSakurasouModelStrategyS3Strategy } from './IoSakurasouModelStrategyS3Strategy';
import {
    IoSakurasouModelStrategyS3StrategyFromJSON,
    IoSakurasouModelStrategyS3StrategyFromJSONTyped,
    IoSakurasouModelStrategyS3StrategyToJSON,
} from './IoSakurasouModelStrategyS3Strategy';
import type { IoSakurasouModelStrategyLocalStrategy } from './IoSakurasouModelStrategyLocalStrategy';
import {
    IoSakurasouModelStrategyLocalStrategyFromJSON,
    IoSakurasouModelStrategyLocalStrategyFromJSONTyped,
    IoSakurasouModelStrategyLocalStrategyToJSON,
} from './IoSakurasouModelStrategyLocalStrategy';
import type { IoSakurasouModelStrategyStrategyType } from './IoSakurasouModelStrategyStrategyType';
import {
    IoSakurasouModelStrategyStrategyTypeFromJSON,
    IoSakurasouModelStrategyStrategyTypeFromJSONTyped,
    IoSakurasouModelStrategyStrategyTypeToJSON,
} from './IoSakurasouModelStrategyStrategyType';

/**
 * 
 * @export
 * @interface IoSakurasouModelStrategyStrategyConfig
 */
export interface IoSakurasouModelStrategyStrategyConfig {
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelStrategyStrategyConfig
     */
    thumbnailFolder: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelStrategyStrategyConfig
     */
    uploadFolder: string;
    /**
     * 
     * @type {IoSakurasouModelStrategyStrategyType}
     * @memberof IoSakurasouModelStrategyStrategyConfig
     */
    strategyType: IoSakurasouModelStrategyStrategyType;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelStrategyStrategyConfig
     */
    accessKey: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelStrategyStrategyConfig
     */
    bucketName: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelStrategyStrategyConfig
     */
    endpoint: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelStrategyStrategyConfig
     */
    publicUrl: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelStrategyStrategyConfig
     */
    region: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelStrategyStrategyConfig
     */
    secretKey: string;
}



/**
 * Check if a given object implements the IoSakurasouModelStrategyStrategyConfig interface.
 */
export function instanceOfIoSakurasouModelStrategyStrategyConfig(value: object): value is IoSakurasouModelStrategyStrategyConfig {
    if (!('thumbnailFolder' in value) || value['thumbnailFolder'] === undefined) return false;
    if (!('uploadFolder' in value) || value['uploadFolder'] === undefined) return false;
    if (!('strategyType' in value) || value['strategyType'] === undefined) return false;
    if (!('accessKey' in value) || value['accessKey'] === undefined) return false;
    if (!('bucketName' in value) || value['bucketName'] === undefined) return false;
    if (!('endpoint' in value) || value['endpoint'] === undefined) return false;
    if (!('publicUrl' in value) || value['publicUrl'] === undefined) return false;
    if (!('region' in value) || value['region'] === undefined) return false;
    if (!('secretKey' in value) || value['secretKey'] === undefined) return false;
    return true;
}

export function IoSakurasouModelStrategyStrategyConfigFromJSON(json: any): IoSakurasouModelStrategyStrategyConfig {
    return IoSakurasouModelStrategyStrategyConfigFromJSONTyped(json, false);
}

export function IoSakurasouModelStrategyStrategyConfigFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouModelStrategyStrategyConfig {
    if (json == null) {
        return json;
    }
    return {
        
        'thumbnailFolder': json['thumbnailFolder'],
        'uploadFolder': json['uploadFolder'],
        'strategyType': IoSakurasouModelStrategyStrategyTypeFromJSON(json['strategyType']),
        'accessKey': json['accessKey'],
        'bucketName': json['bucketName'],
        'endpoint': json['endpoint'],
        'publicUrl': json['publicUrl'],
        'region': json['region'],
        'secretKey': json['secretKey'],
    };
}

export function IoSakurasouModelStrategyStrategyConfigToJSON(value?: IoSakurasouModelStrategyStrategyConfig | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'thumbnailFolder': value['thumbnailFolder'],
        'uploadFolder': value['uploadFolder'],
        'strategyType': IoSakurasouModelStrategyStrategyTypeToJSON(value['strategyType']),
        'accessKey': value['accessKey'],
        'bucketName': value['bucketName'],
        'endpoint': value['endpoint'],
        'publicUrl': value['publicUrl'],
        'region': value['region'],
        'secretKey': value['secretKey'],
    };
}

