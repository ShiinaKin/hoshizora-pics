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



/**
 * 
 * @export
 * @interface UserManagePatchRequest
 */
export interface UserManagePatchRequest {
    /**
     * 
     * @type {number}
     * @memberof UserManagePatchRequest
     */
    'defaultAlbumId'?: number | null;
    /**
     * 
     * @type {string}
     * @memberof UserManagePatchRequest
     */
    'email'?: string | null;
    /**
     * 
     * @type {number}
     * @memberof UserManagePatchRequest
     */
    'groupId'?: number | null;
    /**
     * 
     * @type {boolean}
     * @memberof UserManagePatchRequest
     */
    'isDefaultImagePrivate'?: boolean | null;
    /**
     * 
     * @type {string}
     * @memberof UserManagePatchRequest
     */
    'password'?: string | null;
}

