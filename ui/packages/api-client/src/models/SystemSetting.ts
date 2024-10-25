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
/**
 * 
 * @export
 * @interface SystemSetting
 */
export interface SystemSetting {
    /**
     * 
     * @type {boolean}
     * @memberof SystemSetting
     */
    allowRandomFetch: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof SystemSetting
     */
    allowSignup: boolean;
    /**
     * 
     * @type {number}
     * @memberof SystemSetting
     */
    defaultGroupId: number;
}

/**
 * Check if a given object implements the SystemSetting interface.
 */
export function instanceOfSystemSetting(value: object): value is SystemSetting {
    if (!('allowRandomFetch' in value) || value['allowRandomFetch'] === undefined) return false;
    if (!('allowSignup' in value) || value['allowSignup'] === undefined) return false;
    if (!('defaultGroupId' in value) || value['defaultGroupId'] === undefined) return false;
    return true;
}

export function SystemSettingFromJSON(json: any): SystemSetting {
    return SystemSettingFromJSONTyped(json, false);
}

export function SystemSettingFromJSONTyped(json: any, ignoreDiscriminator: boolean): SystemSetting {
    if (json == null) {
        return json;
    }
    return {
        
        'allowRandomFetch': json['allowRandomFetch'],
        'allowSignup': json['allowSignup'],
        'defaultGroupId': json['defaultGroupId'],
    };
}

  export function SystemSettingToJSON(json: any): SystemSetting {
      return SystemSettingToJSONTyped(json, false);
  }

  export function SystemSettingToJSONTyped(value?: SystemSetting | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'allowRandomFetch': value['allowRandomFetch'],
        'allowSignup': value['allowSignup'],
        'defaultGroupId': value['defaultGroupId'],
    };
}
