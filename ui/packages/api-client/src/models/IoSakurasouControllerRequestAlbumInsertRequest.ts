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
 * @interface IoSakurasouControllerRequestAlbumInsertRequest
 */
export interface IoSakurasouControllerRequestAlbumInsertRequest {
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestAlbumInsertRequest
     */
    description: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestAlbumInsertRequest
     */
    name: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerRequestAlbumInsertRequest interface.
 */
export function instanceOfIoSakurasouControllerRequestAlbumInsertRequest(value: object): value is IoSakurasouControllerRequestAlbumInsertRequest {
    if (!('description' in value) || value['description'] === undefined) return false;
    if (!('name' in value) || value['name'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerRequestAlbumInsertRequestFromJSON(json: any): IoSakurasouControllerRequestAlbumInsertRequest {
    return IoSakurasouControllerRequestAlbumInsertRequestFromJSONTyped(json, false);
}

export function IoSakurasouControllerRequestAlbumInsertRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerRequestAlbumInsertRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'description': json['description'],
        'name': json['name'],
    };
}

export function IoSakurasouControllerRequestAlbumInsertRequestToJSON(value?: IoSakurasouControllerRequestAlbumInsertRequest | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'description': value['description'],
        'name': value['name'],
    };
}
