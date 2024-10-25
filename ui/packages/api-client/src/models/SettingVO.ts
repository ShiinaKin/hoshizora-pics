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
import type { SettingConfigSealed } from './SettingConfigSealed';
import {
    SettingConfigSealedFromJSON,
    SettingConfigSealedFromJSONTyped,
    SettingConfigSealedToJSON,
    SettingConfigSealedToJSONTyped,
} from './SettingConfigSealed';

/**
 * 
 * @export
 * @interface SettingVO
 */
export interface SettingVO {
    /**
     * 
     * @type {SettingConfigSealed}
     * @memberof SettingVO
     */
    config: SettingConfigSealed;
    /**
     * 
     * @type {string}
     * @memberof SettingVO
     */
    name: string;
}

/**
 * Check if a given object implements the SettingVO interface.
 */
export function instanceOfSettingVO(value: object): value is SettingVO {
    if (!('config' in value) || value['config'] === undefined) return false;
    if (!('name' in value) || value['name'] === undefined) return false;
    return true;
}

export function SettingVOFromJSON(json: any): SettingVO {
    return SettingVOFromJSONTyped(json, false);
}

export function SettingVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): SettingVO {
    if (json == null) {
        return json;
    }
    return {
        
        'config': SettingConfigSealedFromJSON(json['config']),
        'name': json['name'],
    };
}

  export function SettingVOToJSON(json: any): SettingVO {
      return SettingVOToJSONTyped(json, false);
  }

  export function SettingVOToJSONTyped(value?: SettingVO | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'config': SettingConfigSealedToJSON(value['config']),
        'name': value['name'],
    };
}
