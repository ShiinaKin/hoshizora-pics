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
 * @interface IoSakurasouControllerRequestUserManagePatchRequest
 */
export interface IoSakurasouControllerRequestUserManagePatchRequest {
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerRequestUserManagePatchRequest
     */
    defaultAlbumId?: number;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestUserManagePatchRequest
     */
    email?: string;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerRequestUserManagePatchRequest
     */
    groupId?: number;
    /**
     * 
     * @type {boolean}
     * @memberof IoSakurasouControllerRequestUserManagePatchRequest
     */
    isDefaultImagePrivate?: boolean;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestUserManagePatchRequest
     */
    password?: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerRequestUserManagePatchRequest interface.
 */
export function instanceOfIoSakurasouControllerRequestUserManagePatchRequest(value: object): value is IoSakurasouControllerRequestUserManagePatchRequest {
    return true;
}

export function IoSakurasouControllerRequestUserManagePatchRequestFromJSON(json: any): IoSakurasouControllerRequestUserManagePatchRequest {
    return IoSakurasouControllerRequestUserManagePatchRequestFromJSONTyped(json, false);
}

export function IoSakurasouControllerRequestUserManagePatchRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerRequestUserManagePatchRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'defaultAlbumId': json['defaultAlbumId'] == null ? undefined : json['defaultAlbumId'],
        'email': json['email'] == null ? undefined : json['email'],
        'groupId': json['groupId'] == null ? undefined : json['groupId'],
        'isDefaultImagePrivate': json['isDefaultImagePrivate'] == null ? undefined : json['isDefaultImagePrivate'],
        'password': json['password'] == null ? undefined : json['password'],
    };
}

export function IoSakurasouControllerRequestUserManagePatchRequestToJSON(value?: IoSakurasouControllerRequestUserManagePatchRequest | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'defaultAlbumId': value['defaultAlbumId'],
        'email': value['email'],
        'groupId': value['groupId'],
        'isDefaultImagePrivate': value['isDefaultImagePrivate'],
        'password': value['password'],
    };
}

