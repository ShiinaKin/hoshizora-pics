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
import type { IoSakurasouControllerRequestGroupConfigUpdatePatch } from './IoSakurasouControllerRequestGroupConfigUpdatePatch';
import {
    IoSakurasouControllerRequestGroupConfigUpdatePatchFromJSON,
    IoSakurasouControllerRequestGroupConfigUpdatePatchFromJSONTyped,
    IoSakurasouControllerRequestGroupConfigUpdatePatchToJSON,
} from './IoSakurasouControllerRequestGroupConfigUpdatePatch';

/**
 * 
 * @export
 * @interface IoSakurasouControllerRequestGroupPatchRequest
 */
export interface IoSakurasouControllerRequestGroupPatchRequest {
    /**
     * 
     * @type {IoSakurasouControllerRequestGroupConfigUpdatePatch}
     * @memberof IoSakurasouControllerRequestGroupPatchRequest
     */
    config?: IoSakurasouControllerRequestGroupConfigUpdatePatch | null;
    /**
     * 
     * @type {any}
     * @memberof IoSakurasouControllerRequestGroupPatchRequest
     */
    description?: any | null;
    /**
     * 
     * @type {any}
     * @memberof IoSakurasouControllerRequestGroupPatchRequest
     */
    name?: any | null;
    /**
     * 
     * @type {any}
     * @memberof IoSakurasouControllerRequestGroupPatchRequest
     */
    roles?: any | null;
    /**
     * 
     * @type {any}
     * @memberof IoSakurasouControllerRequestGroupPatchRequest
     */
    strategyId?: any | null;
}

/**
 * Check if a given object implements the IoSakurasouControllerRequestGroupPatchRequest interface.
 */
export function instanceOfIoSakurasouControllerRequestGroupPatchRequest(value: object): value is IoSakurasouControllerRequestGroupPatchRequest {
    return true;
}

export function IoSakurasouControllerRequestGroupPatchRequestFromJSON(json: any): IoSakurasouControllerRequestGroupPatchRequest {
    return IoSakurasouControllerRequestGroupPatchRequestFromJSONTyped(json, false);
}

export function IoSakurasouControllerRequestGroupPatchRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerRequestGroupPatchRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'config': json['config'] == null ? undefined : IoSakurasouControllerRequestGroupConfigUpdatePatchFromJSON(json['config']),
        'description': json['description'] == null ? undefined : json['description'],
        'name': json['name'] == null ? undefined : json['name'],
        'roles': json['roles'] == null ? undefined : json['roles'],
        'strategyId': json['strategyId'] == null ? undefined : json['strategyId'],
    };
}

export function IoSakurasouControllerRequestGroupPatchRequestToJSON(value?: IoSakurasouControllerRequestGroupPatchRequest | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'config': IoSakurasouControllerRequestGroupConfigUpdatePatchToJSON(value['config']),
        'description': value['description'],
        'name': value['name'],
        'roles': value['roles'],
        'strategyId': value['strategyId'],
    };
}

