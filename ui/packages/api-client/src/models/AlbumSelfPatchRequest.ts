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
 * @interface AlbumSelfPatchRequest
 */
export interface AlbumSelfPatchRequest {
    /**
     * 
     * @type {string}
     * @memberof AlbumSelfPatchRequest
     */
    description?: string | null;
    /**
     * 
     * @type {string}
     * @memberof AlbumSelfPatchRequest
     */
    name?: string | null;
}

/**
 * Check if a given object implements the AlbumSelfPatchRequest interface.
 */
export function instanceOfAlbumSelfPatchRequest(value: object): value is AlbumSelfPatchRequest {
    return true;
}

export function AlbumSelfPatchRequestFromJSON(json: any): AlbumSelfPatchRequest {
    return AlbumSelfPatchRequestFromJSONTyped(json, false);
}

export function AlbumSelfPatchRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): AlbumSelfPatchRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'description': json['description'] == null ? undefined : json['description'],
        'name': json['name'] == null ? undefined : json['name'],
    };
}

  export function AlbumSelfPatchRequestToJSON(json: any): AlbumSelfPatchRequest {
      return AlbumSelfPatchRequestToJSONTyped(json, false);
  }

  export function AlbumSelfPatchRequestToJSONTyped(value?: AlbumSelfPatchRequest | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'description': value['description'],
        'name': value['name'],
    };
}
