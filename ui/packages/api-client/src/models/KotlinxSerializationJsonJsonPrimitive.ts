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
import type { KotlinxSerializationJsonJsonLiteral } from './KotlinxSerializationJsonJsonLiteral';
import {
    KotlinxSerializationJsonJsonLiteralFromJSON,
    KotlinxSerializationJsonJsonLiteralFromJSONTyped,
    KotlinxSerializationJsonJsonLiteralToJSON,
} from './KotlinxSerializationJsonJsonLiteral';
import type { KotlinxSerializationJsonJsonNull } from './KotlinxSerializationJsonJsonNull';
import {
    KotlinxSerializationJsonJsonNullFromJSON,
    KotlinxSerializationJsonJsonNullFromJSONTyped,
    KotlinxSerializationJsonJsonNullToJSON,
} from './KotlinxSerializationJsonJsonNull';

/**
 * 
 * @export
 * @interface KotlinxSerializationJsonJsonPrimitive
 */
export interface KotlinxSerializationJsonJsonPrimitive {
    /**
     * 
     * @type {string}
     * @memberof KotlinxSerializationJsonJsonPrimitive
     */
    content: string;
    /**
     * 
     * @type {boolean}
     * @memberof KotlinxSerializationJsonJsonPrimitive
     */
    isString: boolean;
}

/**
 * Check if a given object implements the KotlinxSerializationJsonJsonPrimitive interface.
 */
export function instanceOfKotlinxSerializationJsonJsonPrimitive(value: object): value is KotlinxSerializationJsonJsonPrimitive {
    if (!('content' in value) || value['content'] === undefined) return false;
    if (!('isString' in value) || value['isString'] === undefined) return false;
    return true;
}

export function KotlinxSerializationJsonJsonPrimitiveFromJSON(json: any): KotlinxSerializationJsonJsonPrimitive {
    return KotlinxSerializationJsonJsonPrimitiveFromJSONTyped(json, false);
}

export function KotlinxSerializationJsonJsonPrimitiveFromJSONTyped(json: any, ignoreDiscriminator: boolean): KotlinxSerializationJsonJsonPrimitive {
    if (json == null) {
        return json;
    }
    return {
        
        'content': json['content'],
        'isString': json['isString'],
    };
}

export function KotlinxSerializationJsonJsonPrimitiveToJSON(value?: KotlinxSerializationJsonJsonPrimitive | null): any {
    if (value == null) {
        return value;
    }
    return {
        
        'content': value['content'],
        'isString': value['isString'],
    };
}

