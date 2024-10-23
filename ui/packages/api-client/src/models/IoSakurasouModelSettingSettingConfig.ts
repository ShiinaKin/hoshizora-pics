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
import type { IoSakurasouModelSettingSystemSetting } from './IoSakurasouModelSettingSystemSetting';
import {
    IoSakurasouModelSettingSystemSettingFromJSON,
    IoSakurasouModelSettingSystemSettingFromJSONTyped,
    IoSakurasouModelSettingSystemSettingToJSON,
} from './IoSakurasouModelSettingSystemSetting';
import type { IoSakurasouModelSettingSiteSetting } from './IoSakurasouModelSettingSiteSetting';
import {
    IoSakurasouModelSettingSiteSettingFromJSON,
    IoSakurasouModelSettingSiteSettingFromJSONTyped,
    IoSakurasouModelSettingSiteSettingToJSON,
} from './IoSakurasouModelSettingSiteSetting';
import type { IoSakurasouModelSettingSystemStatus } from './IoSakurasouModelSettingSystemStatus';
import {
    IoSakurasouModelSettingSystemStatusFromJSON,
    IoSakurasouModelSettingSystemStatusFromJSONTyped,
    IoSakurasouModelSettingSystemStatusToJSON,
} from './IoSakurasouModelSettingSystemStatus';

/**
 * 
 * @export
 * @interface IoSakurasouModelSettingSettingConfig
 */
export interface IoSakurasouModelSettingSettingConfig {
    /**
     * 
     * @type {boolean}
     * @memberof IoSakurasouModelSettingSettingConfig
     */
    homePageRandomPicDisplay: boolean;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelSettingSettingConfig
     */
    siteDescription: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelSettingSettingConfig
     */
    siteExternalUrl: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelSettingSettingConfig
     */
    siteKeyword: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelSettingSettingConfig
     */
    siteSubtitle: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouModelSettingSettingConfig
     */
    siteTitle: string;
    /**
     * 
     * @type {boolean}
     * @memberof IoSakurasouModelSettingSettingConfig
     */
    allowRandomFetch: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof IoSakurasouModelSettingSettingConfig
     */
    allowSignup: boolean;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouModelSettingSettingConfig
     */
    defaultGroupId: number;
    /**
     * 
     * @type {boolean}
     * @memberof IoSakurasouModelSettingSettingConfig
     */
    isInit: boolean;
}

/**
 * Check if a given object implements the IoSakurasouModelSettingSettingConfig interface.
 */
export function instanceOfIoSakurasouModelSettingSettingConfig(value: object): value is IoSakurasouModelSettingSettingConfig {
    if (!('homePageRandomPicDisplay' in value) || value['homePageRandomPicDisplay'] === undefined) return false;
    if (!('siteDescription' in value) || value['siteDescription'] === undefined) return false;
    if (!('siteExternalUrl' in value) || value['siteExternalUrl'] === undefined) return false;
    if (!('siteKeyword' in value) || value['siteKeyword'] === undefined) return false;
    if (!('siteSubtitle' in value) || value['siteSubtitle'] === undefined) return false;
    if (!('siteTitle' in value) || value['siteTitle'] === undefined) return false;
    if (!('allowRandomFetch' in value) || value['allowRandomFetch'] === undefined) return false;
    if (!('allowSignup' in value) || value['allowSignup'] === undefined) return false;
    if (!('defaultGroupId' in value) || value['defaultGroupId'] === undefined) return false;
    if (!('isInit' in value) || value['isInit'] === undefined) return false;
    return true;
}

export function IoSakurasouModelSettingSettingConfigFromJSON(json: any): IoSakurasouModelSettingSettingConfig {
    return IoSakurasouModelSettingSettingConfigFromJSONTyped(json, false);
}

export function IoSakurasouModelSettingSettingConfigFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouModelSettingSettingConfig {
    if (json == null) {
        return json;
    }
    return {
        
        'homePageRandomPicDisplay': json['homePageRandomPicDisplay'],
        'siteDescription': json['siteDescription'],
        'siteExternalUrl': json['siteExternalUrl'],
        'siteKeyword': json['siteKeyword'],
        'siteSubtitle': json['siteSubtitle'],
        'siteTitle': json['siteTitle'],
        'allowRandomFetch': json['allowRandomFetch'],
        'allowSignup': json['allowSignup'],
        'defaultGroupId': json['defaultGroupId'],
        'isInit': json['isInit'],
    };
}

export function IoSakurasouModelSettingSettingConfigToJSON(value?: IoSakurasouModelSettingSettingConfig | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'homePageRandomPicDisplay': value['homePageRandomPicDisplay'],
        'siteDescription': value['siteDescription'],
        'siteExternalUrl': value['siteExternalUrl'],
        'siteKeyword': value['siteKeyword'],
        'siteSubtitle': value['siteSubtitle'],
        'siteTitle': value['siteTitle'],
        'allowRandomFetch': value['allowRandomFetch'],
        'allowSignup': value['allowSignup'],
        'defaultGroupId': value['defaultGroupId'],
        'isInit': value['isInit'],
    };
}

