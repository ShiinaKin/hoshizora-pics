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


// May contain unused imports in some cases
// @ts-ignore
import type { PageResultAlbumPageVO } from './page-result-album-page-vo';

/**
 * 
 * @export
 * @interface CommonResponsePageResultAlbumPageVO
 */
export interface CommonResponsePageResultAlbumPageVO {
    /**
     * 
     * @type {number}
     * @memberof CommonResponsePageResultAlbumPageVO
     */
    'code': number;
    /**
     * 
     * @type {PageResultAlbumPageVO}
     * @memberof CommonResponsePageResultAlbumPageVO
     */
    'data'?: PageResultAlbumPageVO | null;
    /**
     * 
     * @type {boolean}
     * @memberof CommonResponsePageResultAlbumPageVO
     */
    'isSuccessful': boolean;
    /**
     * 
     * @type {string}
     * @memberof CommonResponsePageResultAlbumPageVO
     */
    'message': string;
}

