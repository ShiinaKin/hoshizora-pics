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
 * @interface CommonResponseSettingVOMap
 */
export interface CommonResponseSettingVOMap {
    /**
     * 
     * @type {any}
     * @memberof CommonResponseSettingVOMap
     */
    code?: any | null;
    /**
     * 
     * @type {any}
     * @memberof CommonResponseSettingVOMap
     */
    message?: any | null;
    /**
     * 
     * @type {{ [key: string]: any; }}
     * @memberof CommonResponseSettingVOMap
     */
    data?: { [key: string]: any; } | null;
    /**
     * 
     * @type {any}
     * @memberof CommonResponseSettingVOMap
     */
    isSuccessful?: any | null;
}

/**
 * Check if a given object implements the CommonResponseSettingVOMap interface.
 */
export function instanceOfCommonResponseSettingVOMap(value: object): value is CommonResponseSettingVOMap {
    return true;
}

export function CommonResponseSettingVOMapFromJSON(json: any): CommonResponseSettingVOMap {
    return CommonResponseSettingVOMapFromJSONTyped(json, false);
}

export function CommonResponseSettingVOMapFromJSONTyped(json: any, ignoreDiscriminator: boolean): CommonResponseSettingVOMap {
    if (json == null) {
        return json;
    }
    return {
        
        'code': json['code'] == null ? undefined : json['code'],
        'message': json['message'] == null ? undefined : json['message'],
        'data': json['data'] == null ? undefined : json['data'],
        'isSuccessful': json['isSuccessful'] == null ? undefined : json['isSuccessful'],
    };
}

  export function CommonResponseSettingVOMapToJSON(json: any): CommonResponseSettingVOMap {
      return CommonResponseSettingVOMapToJSONTyped(json, false);
  }

  export function CommonResponseSettingVOMapToJSONTyped(value?: CommonResponseSettingVOMap | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'code': value['code'],
        'message': value['message'],
        'data': value['data'],
        'isSuccessful': value['isSuccessful'],
    };
}

