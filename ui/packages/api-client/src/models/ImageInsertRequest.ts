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
 * @interface ImageInsertRequest
 */
export interface ImageInsertRequest {
    /**
     * 
     * @type {number}
     * @memberof ImageInsertRequest
     */
    albumId?: number | null;
    /**
     * 
     * @type {string}
     * @memberof ImageInsertRequest
     */
    description?: string | null;
    /**
     * 
     * @type {string}
     * @memberof ImageInsertRequest
     */
    displayName?: string | null;
    /**
     * 
     * @type {boolean}
     * @memberof ImageInsertRequest
     */
    isPrivate?: boolean | null;
}

/**
 * Check if a given object implements the ImageInsertRequest interface.
 */
export function instanceOfImageInsertRequest(value: object): value is ImageInsertRequest {
    return true;
}

export function ImageInsertRequestFromJSON(json: any): ImageInsertRequest {
    return ImageInsertRequestFromJSONTyped(json, false);
}

export function ImageInsertRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): ImageInsertRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'albumId': json['albumId'] == null ? undefined : json['albumId'],
        'description': json['description'] == null ? undefined : json['description'],
        'displayName': json['displayName'] == null ? undefined : json['displayName'],
        'isPrivate': json['isPrivate'] == null ? undefined : json['isPrivate'],
    };
}

  export function ImageInsertRequestToJSON(json: any): ImageInsertRequest {
      return ImageInsertRequestToJSONTyped(json, false);
  }

  export function ImageInsertRequestToJSONTyped(value?: ImageInsertRequest | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'albumId': value['albumId'],
        'description': value['description'],
        'displayName': value['displayName'],
        'isPrivate': value['isPrivate'],
    };
}
