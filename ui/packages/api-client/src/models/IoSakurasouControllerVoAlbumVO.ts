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
 * @interface IoSakurasouControllerVoAlbumVO
 */
export interface IoSakurasouControllerVoAlbumVO {
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerVoAlbumVO
     */
    description?: string;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoAlbumVO
     */
    id: number;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoAlbumVO
     */
    imageCount: number;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerVoAlbumVO
     */
    name: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerVoAlbumVO interface.
 */
export function instanceOfIoSakurasouControllerVoAlbumVO(value: object): value is IoSakurasouControllerVoAlbumVO {
    if (!('id' in value) || value['id'] === undefined) return false;
    if (!('imageCount' in value) || value['imageCount'] === undefined) return false;
    if (!('name' in value) || value['name'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoAlbumVOFromJSON(json: any): IoSakurasouControllerVoAlbumVO {
    return IoSakurasouControllerVoAlbumVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoAlbumVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoAlbumVO {
    if (json == null) {
        return json;
    }
    return {
        
        'description': json['description'] == null ? undefined : json['description'],
        'id': json['id'],
        'imageCount': json['imageCount'],
        'name': json['name'],
    };
}

export function IoSakurasouControllerVoAlbumVOToJSON(value?: IoSakurasouControllerVoAlbumVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'description': value['description'],
        'id': value['id'],
        'imageCount': value['imageCount'],
        'name': value['name'],
    };
}
