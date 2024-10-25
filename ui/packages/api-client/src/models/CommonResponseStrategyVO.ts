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
import type { StrategyVO } from './StrategyVO';
import {
    StrategyVOFromJSON,
    StrategyVOFromJSONTyped,
    StrategyVOToJSON,
    StrategyVOToJSONTyped,
} from './StrategyVO';

/**
 * 
 * @export
 * @interface CommonResponseStrategyVO
 */
export interface CommonResponseStrategyVO {
    /**
     * 
     * @type {number}
     * @memberof CommonResponseStrategyVO
     */
    code: number;
    /**
     * 
     * @type {StrategyVO}
     * @memberof CommonResponseStrategyVO
     */
    data?: StrategyVO | null;
    /**
     * 
     * @type {boolean}
     * @memberof CommonResponseStrategyVO
     */
    isSuccessful: boolean;
    /**
     * 
     * @type {string}
     * @memberof CommonResponseStrategyVO
     */
    message: string;
}

/**
 * Check if a given object implements the CommonResponseStrategyVO interface.
 */
export function instanceOfCommonResponseStrategyVO(value: object): value is CommonResponseStrategyVO {
    if (!('code' in value) || value['code'] === undefined) return false;
    if (!('isSuccessful' in value) || value['isSuccessful'] === undefined) return false;
    if (!('message' in value) || value['message'] === undefined) return false;
    return true;
}

export function CommonResponseStrategyVOFromJSON(json: any): CommonResponseStrategyVO {
    return CommonResponseStrategyVOFromJSONTyped(json, false);
}

export function CommonResponseStrategyVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): CommonResponseStrategyVO {
    if (json == null) {
        return json;
    }
    return {
        
        'code': json['code'],
        'data': json['data'] == null ? undefined : StrategyVOFromJSON(json['data']),
        'isSuccessful': json['isSuccessful'],
        'message': json['message'],
    };
}

  export function CommonResponseStrategyVOToJSON(json: any): CommonResponseStrategyVO {
      return CommonResponseStrategyVOToJSONTyped(json, false);
  }

  export function CommonResponseStrategyVOToJSONTyped(value?: CommonResponseStrategyVO | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'code': value['code'],
        'data': StrategyVOToJSON(value['data']),
        'isSuccessful': value['isSuccessful'],
        'message': value['message'],
    };
}

