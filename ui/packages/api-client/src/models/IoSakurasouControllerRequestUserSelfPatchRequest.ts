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
 * @interface IoSakurasouControllerRequestUserSelfPatchRequest
 */
export interface IoSakurasouControllerRequestUserSelfPatchRequest {
    /**
     * 
     * @type {any}
     * @memberof IoSakurasouControllerRequestUserSelfPatchRequest
     */
    defaultAlbumId?: any | null;
    /**
     * 
     * @type {any}
     * @memberof IoSakurasouControllerRequestUserSelfPatchRequest
     */
    email?: any | null;
    /**
     * 
     * @type {any}
     * @memberof IoSakurasouControllerRequestUserSelfPatchRequest
     */
    isDefaultImagePrivate?: any | null;
    /**
     * 
     * @type {any}
     * @memberof IoSakurasouControllerRequestUserSelfPatchRequest
     */
    password?: any | null;
}

/**
 * Check if a given object implements the IoSakurasouControllerRequestUserSelfPatchRequest interface.
 */
export function instanceOfIoSakurasouControllerRequestUserSelfPatchRequest(value: object): value is IoSakurasouControllerRequestUserSelfPatchRequest {
    return true;
}

export function IoSakurasouControllerRequestUserSelfPatchRequestFromJSON(json: any): IoSakurasouControllerRequestUserSelfPatchRequest {
    return IoSakurasouControllerRequestUserSelfPatchRequestFromJSONTyped(json, false);
}

export function IoSakurasouControllerRequestUserSelfPatchRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerRequestUserSelfPatchRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'defaultAlbumId': json['defaultAlbumId'] == null ? undefined : json['defaultAlbumId'],
        'email': json['email'] == null ? undefined : json['email'],
        'isDefaultImagePrivate': json['isDefaultImagePrivate'] == null ? undefined : json['isDefaultImagePrivate'],
        'password': json['password'] == null ? undefined : json['password'],
    };
}

export function IoSakurasouControllerRequestUserSelfPatchRequestToJSON(value?: IoSakurasouControllerRequestUserSelfPatchRequest | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'defaultAlbumId': value['defaultAlbumId'],
        'email': value['email'],
        'isDefaultImagePrivate': value['isDefaultImagePrivate'],
        'password': value['password'],
    };
}

