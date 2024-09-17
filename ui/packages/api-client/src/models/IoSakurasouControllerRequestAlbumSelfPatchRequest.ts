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
 * @interface IoSakurasouControllerRequestAlbumSelfPatchRequest
 */
export interface IoSakurasouControllerRequestAlbumSelfPatchRequest {
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestAlbumSelfPatchRequest
     */
    description?: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestAlbumSelfPatchRequest
     */
    name?: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerRequestAlbumSelfPatchRequest interface.
 */
export function instanceOfIoSakurasouControllerRequestAlbumSelfPatchRequest(value: object): value is IoSakurasouControllerRequestAlbumSelfPatchRequest {
    return true;
}

export function IoSakurasouControllerRequestAlbumSelfPatchRequestFromJSON(json: any): IoSakurasouControllerRequestAlbumSelfPatchRequest {
    return IoSakurasouControllerRequestAlbumSelfPatchRequestFromJSONTyped(json, false);
}

export function IoSakurasouControllerRequestAlbumSelfPatchRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerRequestAlbumSelfPatchRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'description': json['description'] == null ? undefined : json['description'],
        'name': json['name'] == null ? undefined : json['name'],
    };
}

export function IoSakurasouControllerRequestAlbumSelfPatchRequestToJSON(value?: IoSakurasouControllerRequestAlbumSelfPatchRequest | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'description': value['description'],
        'name': value['name'],
    };
}
