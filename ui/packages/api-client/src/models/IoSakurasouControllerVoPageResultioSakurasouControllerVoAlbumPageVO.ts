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
import type { IoSakurasouControllerVoAlbumPageVO } from './IoSakurasouControllerVoAlbumPageVO';
import {
    IoSakurasouControllerVoAlbumPageVOFromJSON,
    IoSakurasouControllerVoAlbumPageVOFromJSONTyped,
    IoSakurasouControllerVoAlbumPageVOToJSON,
} from './IoSakurasouControllerVoAlbumPageVO';

/**
 * 
 * @export
 * @interface IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVO
 */
export interface IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVO {
    /**
     * 
     * @type {Array<IoSakurasouControllerVoAlbumPageVO>}
     * @memberof IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVO
     */
    list: Array<IoSakurasouControllerVoAlbumPageVO>;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVO
     */
    total: number;
}

/**
 * Check if a given object implements the IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVO interface.
 */
export function instanceOfIoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVO(value: object): value is IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVO {
    if (!('list' in value) || value['list'] === undefined) return false;
    if (!('total' in value) || value['total'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVOFromJSON(json: any): IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVO {
    return IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVOFromJSONTyped(json, false);
}

export function IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVOFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVO {
    if (json == null) {
        return json;
    }
    return {
        
        'list': ((json['list'] as Array<any>).map(IoSakurasouControllerVoAlbumPageVOFromJSON)),
        'total': json['total'],
    };
}

export function IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVOToJSON(value?: IoSakurasouControllerVoPageResultioSakurasouControllerVoAlbumPageVO | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'list': ((value['list'] as Array<any>).map(IoSakurasouControllerVoAlbumPageVOToJSON)),
        'total': value['total'],
    };
}

