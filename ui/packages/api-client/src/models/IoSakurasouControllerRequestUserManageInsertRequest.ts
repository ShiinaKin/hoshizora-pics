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
 * @interface IoSakurasouControllerRequestUserManageInsertRequest
 */
export interface IoSakurasouControllerRequestUserManageInsertRequest {
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestUserManageInsertRequest
     */
    email: string;
    /**
     * 
     * @type {number}
     * @memberof IoSakurasouControllerRequestUserManageInsertRequest
     */
    groupId: number;
    /**
     * 
     * @type {boolean}
     * @memberof IoSakurasouControllerRequestUserManageInsertRequest
     */
    isDefaultImagePrivate: boolean;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestUserManageInsertRequest
     */
    password: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestUserManageInsertRequest
     */
    username: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerRequestUserManageInsertRequest interface.
 */
export function instanceOfIoSakurasouControllerRequestUserManageInsertRequest(value: object): value is IoSakurasouControllerRequestUserManageInsertRequest {
    if (!('email' in value) || value['email'] === undefined) return false;
    if (!('groupId' in value) || value['groupId'] === undefined) return false;
    if (!('isDefaultImagePrivate' in value) || value['isDefaultImagePrivate'] === undefined) return false;
    if (!('password' in value) || value['password'] === undefined) return false;
    if (!('username' in value) || value['username'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerRequestUserManageInsertRequestFromJSON(json: any): IoSakurasouControllerRequestUserManageInsertRequest {
    return IoSakurasouControllerRequestUserManageInsertRequestFromJSONTyped(json, false);
}

export function IoSakurasouControllerRequestUserManageInsertRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerRequestUserManageInsertRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'email': json['email'],
        'groupId': json['groupId'],
        'isDefaultImagePrivate': json['isDefaultImagePrivate'],
        'password': json['password'],
        'username': json['username'],
    };
}

export function IoSakurasouControllerRequestUserManageInsertRequestToJSON(value?: IoSakurasouControllerRequestUserManageInsertRequest | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'email': value['email'],
        'groupId': value['groupId'],
        'isDefaultImagePrivate': value['isDefaultImagePrivate'],
        'password': value['password'],
        'username': value['username'],
    };
}

