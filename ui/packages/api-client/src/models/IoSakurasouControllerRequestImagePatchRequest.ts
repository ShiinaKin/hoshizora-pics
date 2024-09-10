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
 * @interface IoSakurasouControllerRequestImagePatchRequest
 */
export interface IoSakurasouControllerRequestImagePatchRequest {
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestImagePatchRequest
     */
    description?: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestImagePatchRequest
     */
    originName?: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerRequestImagePatchRequest interface.
 */
export function instanceOfIoSakurasouControllerRequestImagePatchRequest(value: object): value is IoSakurasouControllerRequestImagePatchRequest {
    return true;
}

export function IoSakurasouControllerRequestImagePatchRequestFromJSON(json: any): IoSakurasouControllerRequestImagePatchRequest {
    return IoSakurasouControllerRequestImagePatchRequestFromJSONTyped(json, false);
}

export function IoSakurasouControllerRequestImagePatchRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerRequestImagePatchRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'description': json['description'] == null ? undefined : json['description'],
        'originName': json['originName'] == null ? undefined : json['originName'],
    };
}

export function IoSakurasouControllerRequestImagePatchRequestToJSON(value?: IoSakurasouControllerRequestImagePatchRequest | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'description': value['description'],
        'originName': value['originName'],
    };
}

