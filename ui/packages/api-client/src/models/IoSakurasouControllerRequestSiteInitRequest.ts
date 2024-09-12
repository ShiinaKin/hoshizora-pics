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
 * @interface IoSakurasouControllerRequestSiteInitRequest
 */
export interface IoSakurasouControllerRequestSiteInitRequest {
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestSiteInitRequest
     */
    email: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestSiteInitRequest
     */
    password: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestSiteInitRequest
     */
    siteSubtitle: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestSiteInitRequest
     */
    siteTitle: string;
    /**
     * 
     * @type {string}
     * @memberof IoSakurasouControllerRequestSiteInitRequest
     */
    username: string;
}

/**
 * Check if a given object implements the IoSakurasouControllerRequestSiteInitRequest interface.
 */
export function instanceOfIoSakurasouControllerRequestSiteInitRequest(value: object): value is IoSakurasouControllerRequestSiteInitRequest {
    if (!('email' in value) || value['email'] === undefined) return false;
    if (!('password' in value) || value['password'] === undefined) return false;
    if (!('siteSubtitle' in value) || value['siteSubtitle'] === undefined) return false;
    if (!('siteTitle' in value) || value['siteTitle'] === undefined) return false;
    if (!('username' in value) || value['username'] === undefined) return false;
    return true;
}

export function IoSakurasouControllerRequestSiteInitRequestFromJSON(json: any): IoSakurasouControllerRequestSiteInitRequest {
    return IoSakurasouControllerRequestSiteInitRequestFromJSONTyped(json, false);
}

export function IoSakurasouControllerRequestSiteInitRequestFromJSONTyped(json: any, ignoreDiscriminator: boolean): IoSakurasouControllerRequestSiteInitRequest {
    if (json == null) {
        return json;
    }
    return {
        
        'email': json['email'],
        'password': json['password'],
        'siteSubtitle': json['siteSubtitle'],
        'siteTitle': json['siteTitle'],
        'username': json['username'],
    };
}

export function IoSakurasouControllerRequestSiteInitRequestToJSON(value?: IoSakurasouControllerRequestSiteInitRequest | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'email': value['email'],
        'password': value['password'],
        'siteSubtitle': value['siteSubtitle'],
        'siteTitle': value['siteTitle'],
        'username': value['username'],
    };
}
