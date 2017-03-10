/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package hydrograph.engine.spark.generaterecord.utils

import java.math.BigDecimal

/**
  * The Class FieldsTypeEnum.
  *
  * @author Bitwise
  *
  */
object FieldsTypeEnum {

 
type TypeSelection = FieldEntity => Any

  object FieldTypeEnum {
    
 
    object INTEGER extends TypeSelection {
      override def apply(fieldEntity: FieldEntity): Any = {
        
          var value: Int = 0

          if (fieldEntity.defaultValue != null) {
            value = DataGenerator.getDefaultInteger(fieldEntity.defaultValue.toInt)
          } else if (fieldEntity.rangeFromValue != null
            && fieldEntity.rangeToValue != null) {
            value = DataGenerator.getIntegerBetween(
              fieldEntity.rangeFromValue.toInt,
              fieldEntity.rangeToValue.toInt);
          } else if (fieldEntity.rangeFromValue != null && fieldEntity.fieldLength != 0) {
            value = DataGenerator.getIntegerBetween(
              fieldEntity.rangeFromValue.toInt,
              (Math.pow(10, fieldEntity.fieldLength) - 1).toInt);
          } else if (fieldEntity.rangeFromValue != null) {
            value = DataGenerator.getFromInteger(fieldEntity.rangeFromValue.toInt);
          } else if (fieldEntity.rangeToValue != null) {
            value = DataGenerator.getToInteger(fieldEntity.rangeToValue.toInt);
          } else if (fieldEntity.fieldLength != 0) {
            value = DataGenerator.getRandomInteger(fieldEntity.fieldLength);
          } else {
            value = DataGenerator.getRandomInteger();
          }
          return value;
        
      }
    }
    object STRING extends TypeSelection {
      override def apply(fieldEntity: FieldEntity): Any = {

           var value: String = "";

            if (fieldEntity.defaultValue != null) {
              value = DataGenerator.getDefaultString(fieldEntity.defaultValue);
            } else if (fieldEntity.fieldLength != 0) {
              value = DataGenerator.getRandomString(fieldEntity.fieldLength);
            } else {
              value = DataGenerator.getRandomString();
            }
            return value;
        
        
      }

    }
    
    
      object BIGDECIMAL extends TypeSelection {
      override def apply(fieldEntity: FieldEntity): Any = {
        
         var value:BigDecimal=BigDecimal.ZERO
          
          if (fieldEntity.defaultValue != null) {
          value = DataGenerator.getDefaultBigDecimal(new BigDecimal(fieldEntity.defaultValue.toInt), fieldEntity.fieldScale);
          } else if (fieldEntity.fieldLength != 0) {
          value = DataGenerator.getRandomBigDecimal(
              fieldEntity.fieldScale,
              fieldEntity.fieldLength);
          } else if (fieldEntity.rangeFromValue != null
            && fieldEntity.rangeToValue != null) {
           value = DataGenerator.getBigDecimalBetween(
              new BigDecimal(
                fieldEntity.rangeFromValue.toInt), new BigDecimal(
                fieldEntity.rangeToValue.toInt), fieldEntity.fieldScale);
          } else if (fieldEntity.rangeFromValue != null) {
          val  value = DataGenerator.getFromBigDecimal(new BigDecimal(
              fieldEntity.rangeFromValue.toInt), fieldEntity.fieldScale)
          } else if (fieldEntity.rangeToValue != null) {
          val  value = DataGenerator.getToBigDecimal(new BigDecimal(
              fieldEntity.rangeToValue.toInt), fieldEntity.fieldScale)
          } else {
          value = DataGenerator.getRandomBigDecimal(fieldEntity.fieldScale)
          }
          
          return value;
        
        
      }
    }
      
       object DATE extends TypeSelection {
      override def apply(fieldEntity: FieldEntity): Any = {
         var value: String = ""

          if (fieldEntity.defaultValue != null) {
            value = DataGenerator.getDefaultDate(
              fieldEntity.fieldFormat,
              fieldEntity.defaultValue)
          } else if (fieldEntity.rangeFromValue != null
            && fieldEntity.rangeToValue != null) {
            value = DataGenerator.getDateBetween(
              fieldEntity.fieldFormat,
              fieldEntity.rangeFromValue,
              fieldEntity.rangeToValue);
          } else if (fieldEntity.rangeFromValue != null) {
            value = DataGenerator.getFromDate(
              fieldEntity.fieldFormat,
              fieldEntity.rangeFromValue);
          } else if (fieldEntity.rangeToValue != null) {
            value = DataGenerator.getToDate(
              fieldEntity.fieldFormat,
              fieldEntity.rangeToValue);
          } else {
            value = DataGenerator.getRandomDate(fieldEntity.fieldFormat);
          }
     return value
        
      }
    }
       
        object FLOAT extends TypeSelection {
          
      override def apply(fieldEntity: FieldEntity): Any = {
        
           var value: Float = 0
          if (fieldEntity.defaultValue != null) {
            value = DataGenerator.getDefaultFloat(fieldEntity.defaultValue.toFloat);
          } else if (fieldEntity.rangeFromValue != null
            && fieldEntity.rangeToValue != null) {
            value = DataGenerator.getFloatBetween(
              fieldEntity.rangeFromValue.toFloat,
              fieldEntity.rangeToValue.toFloat);
          } else if (fieldEntity.rangeFromValue != null && fieldEntity.fieldLength != 0) {
            value = DataGenerator.getFloatBetween(
              fieldEntity.rangeFromValue.toFloat,
              (Math.pow(10, fieldEntity.fieldLength).toFloat - 1));
          } else if (fieldEntity.rangeFromValue != null) {
            value = DataGenerator.getFromFloat(fieldEntity
              .rangeFromValue.toFloat);
          } else if (fieldEntity.rangeToValue != null) {
            value = DataGenerator.getToFloat(fieldEntity.rangeToValue.toFloat);
          } else if (fieldEntity.fieldLength != 0) {
            value = DataGenerator.getRandomFloat(fieldEntity.fieldLength);
          } else {
            value = DataGenerator.getRandomFloat();
          }
          return value;
        
      }
    }
        
           object SHORT extends TypeSelection {
      override def apply(fieldEntity: FieldEntity): Any = {
        
        
         var value: Short = 0;
          if (fieldEntity.defaultValue != null) {
            value = DataGenerator.getDefaultShort(fieldEntity.defaultValue.toShort);
          } else if (fieldEntity.rangeFromValue != null
            && fieldEntity.rangeToValue != null) {
            value = DataGenerator.getShortBetween(
              fieldEntity.rangeFromValue.toShort,
              fieldEntity.rangeToValue.toShort);
          } else if (fieldEntity.rangeFromValue != null && fieldEntity.fieldLength != 0) {
            value = DataGenerator.getShortBetween(
              fieldEntity.rangeFromValue.toShort,
              (Math.pow(10, fieldEntity.fieldLength) - 1).toShort);
          } else if (fieldEntity.rangeFromValue != null) {
            value = DataGenerator.getFromShort((fieldEntity.rangeFromValue).toShort)
          } else if (fieldEntity.rangeToValue != null) {
            value = DataGenerator.getToShort((fieldEntity.rangeToValue.toShort));
          } else if (fieldEntity.fieldLength != 0) {
            value = DataGenerator.getRandomShort(fieldEntity.fieldLength).toShort
          } else {
            value = DataGenerator.getRandomShort();
          }
          return value;
        
      }
    }
           
              object LONG extends TypeSelection {
      override def apply(fieldEntity: FieldEntity): Any = {
        
        
           var value: Long = 0;
          if (fieldEntity.defaultValue != null) {
            value = DataGenerator.getDefaultLong((fieldEntity.defaultValue).toLong);
          } else if (fieldEntity.rangeFromValue != null
            && fieldEntity.rangeToValue != null) {
            value = DataGenerator.getLongBetween(
              (fieldEntity.rangeFromValue).toLong,
              (fieldEntity.rangeToValue).toLong);
          } else if (fieldEntity.rangeFromValue != null && fieldEntity.fieldLength != 0) {
            value = DataGenerator.getLongBetween(
              (fieldEntity.rangeFromValue).toLong,
              (Math.pow(10, fieldEntity.fieldLength) - 1).toLong);
          } else if (fieldEntity.rangeFromValue != null) {
            value = DataGenerator.getFromLong((fieldEntity.rangeFromValue).toLong);
          } else if (fieldEntity.rangeToValue != null) {
            value = DataGenerator.getToLong((fieldEntity.rangeToValue).toLong);
          } else if (fieldEntity.fieldLength != 0) {
            value = DataGenerator.getRandomLong(fieldEntity.fieldLength);
          } else {
            value = DataGenerator.getRandomLong();
          }
          return value;
      }
    }
              
              
                    object DOUBLE extends TypeSelection {
      override def apply(fieldEntity: FieldEntity): Any = {
        
          var value: Double = 0;
          if (fieldEntity.defaultValue != null) {
            value = DataGenerator.getDefaultDouble((fieldEntity.defaultValue).toDouble);
          } else if (fieldEntity.rangeFromValue != null
            && fieldEntity.rangeToValue != null) {
            value = DataGenerator.getDoubleBetween(
              (fieldEntity.rangeFromValue).toDouble,
              (fieldEntity.rangeToValue).toDouble);
          } else if (fieldEntity.rangeFromValue != null && fieldEntity.fieldLength != 0) {
            value = DataGenerator.getDoubleBetween(
              (fieldEntity.rangeFromValue).toDouble,
              (Math.pow(10, fieldEntity.fieldLength) - 1).toDouble);
          } else if (fieldEntity.rangeFromValue != null) {
            value = DataGenerator.getFromDouble((fieldEntity
              .rangeFromValue).toDouble);
          } else if (fieldEntity.rangeToValue != null) {
            value = DataGenerator.getToDouble((fieldEntity.rangeToValue).toDouble);
          } else if (fieldEntity.fieldLength != 0) {
            value = DataGenerator.getRandomDouble(fieldEntity
              .fieldLength)
          } else {
            value = DataGenerator.getRandomDouble();
          }
          return value;
      }
    }
                    
                          object BOOLEAN extends TypeSelection {
      override def apply(fieldEntity: FieldEntity): Any = {
        
        var value: Boolean = false;
						if (fieldEntity.defaultValue != null) {
							value = DataGenerator.getDefaultBoolean((fieldEntity.defaultValue).toBoolean)
						} else {
							value = DataGenerator.getRandomBoolean();
						}
						return value;
      }
    }

  }

}