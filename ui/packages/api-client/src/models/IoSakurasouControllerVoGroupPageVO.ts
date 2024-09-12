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
 * @interface IoSakurasouControllerVoGroupPageVO
 */
export interface IoSakurasouControllerVoGroupPageVO {
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoGroupPageVO
     */
    id: number;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerVoGroupPageVO
     */
    name: string;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoGroupPageVO
     */
    strategyId: number;
}

/**
 * Check if a given object implements the IoSakurasouControllerVoGroupPageVO interface.
 */
export function instanceOfIoSakurasouControllerVoGroupPageVO(value: object): value is IoSakurasouControllerVoGroupPageVO {
    if (!('id' in value) || value['id'] === undefined) return false;
    if (!('name' in value) || value['name'] === undefined) return false;
    if (!('strategyId' in value) || value['strategyId'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoGroupPageVOFromJSON(json: any): IoSakurasouControllerVoGroupPageVO {
    return IoSakurasouControllerVoGroupPageVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoGroupPageVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoGroupPageVO {
    if (json == null) {
        return json;
    }
    return {
        
        'id': json['id'],
        'name': json['name'],
        'strategyId': json['strategyId'],
    };
}

export function IoSakurasouControllerVoGroupPageVOToJSON(value?: IoSakurasouControllerVoGroupPageVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'id': value['id'],
        'name': value['name'],
        'strategyId': value['strategyId'],
    };
}
