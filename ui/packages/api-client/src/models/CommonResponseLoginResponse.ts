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
 * @interface CommonResponseLoginResponse
 */
export interface CommonResponseLoginResponse {
    /**
     * 
     * @type {any}
     * @memberof CommonResponseLoginResponse
     */
    code?: any | null;
    /**
     * 
     * @type {any}
     * @memberof CommonResponseLoginResponse
     */
    message?: any | null;
    /**
     * 
     * @type {{ [key: string]: any; }}
     * @memberof CommonResponseLoginResponse
     */
    data?: { [key: string]: any; } | null;
    /**
     * 
     * @type {any}
     * @memberof CommonResponseLoginResponse
     */
    isSuccessful?: any | null;
}

/**
 * Check if a given object implements the CommonResponseLoginResponse interface.
 */
export function instanceOfCommonResponseLoginResponse(value: object): value is CommonResponseLoginResponse {
    return true;
}

export function CommonResponseLoginResponseFromJSON(json: any): CommonResponseLoginResponse {
    return CommonResponseLoginResponseFromJSONTyped(json, false);
}

export function CommonResponseLoginResponseFromJSONTyped(json: any, ignoreDiscriminator: boolean): CommonResponseLoginResponse {
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

  export function CommonResponseLoginResponseToJSON(json: any): CommonResponseLoginResponse {
      return CommonResponseLoginResponseToJSONTyped(json, false);
  }

  export function CommonResponseLoginResponseToJSONTyped(value?: CommonResponseLoginResponse | null, ignoreDiscriminator: boolean = false): any {
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

