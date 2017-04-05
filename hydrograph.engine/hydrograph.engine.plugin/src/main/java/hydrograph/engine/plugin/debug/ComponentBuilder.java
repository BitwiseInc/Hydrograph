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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.plugin.debug;

import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.core.utilities.SocketUtilities;
import hydrograph.engine.jaxb.commontypes.*;
import hydrograph.engine.jaxb.outputtypes.AvroFile;
import hydrograph.engine.jaxb.outputtypes.AvroFile.Path;
import hydrograph.engine.jaxb.outputtypes.TextFileDelimited;
import hydrograph.engine.jaxb.outputtypes.TextFileDelimited.Charset;
import hydrograph.engine.jaxb.outputtypes.TextFileDelimited.Delimiter;
import hydrograph.engine.jaxb.outputtypes.TextFileDelimited.Quote;
import hydrograph.engine.jaxb.straightpulltypes.Clone;
import hydrograph.engine.jaxb.straightpulltypes.Limit;
import hydrograph.engine.jaxb.straightpulltypes.Limit.MaxRecords;
import hydrograph.engine.plugin.debug.utils.JaxbSchemaFieldConverter;

import java.util.List;
import java.util.Set;
/**
 * The Enum ComponentBuilder.
 *
 * @author Bitwise
 *
 */
public enum ComponentBuilder {
	
	REPLICATE_COMPONENT {

		@Override
		public TypeBaseComponent create(DebugContext debugContext) {
			Clone clone = new Clone();
			clone.setId(ComponentBuilderUtils.generateUniqueComponentId(
					viewDataComponentIdentifier+debugContext.getFromComponentId(),
					debugContext.getFromOutSocketId(),
					debugContext.getTypeBaseComponents()));
			clone.setBatch(debugContext.getBatch());
			clone.getInSocket().add(
					ComponentBuilderUtils.getStraightPullInSocket(
							debugContext.getFromComponentId(),
							debugContext.getFromOutSocketId(),
							debugContext.getFromOutSocketType()));

			clone.getOutSocket().add(
					ComponentBuilderUtils.getStraightPullOutSocket("out0",
							"in0"));
			clone.getOutSocket().add(
					ComponentBuilderUtils.getStraightPullOutSocket("out1",
							"in0"));

			// update the existing component

			TypeBaseComponent component = ComponentBuilderUtils.getComponent(
					debugContext.getTypeBaseComponents(),
					debugContext.getFromComponentId(),
					debugContext.getFromOutSocketId());
			SocketUtilities.updateComponentInSocket(component,
					debugContext.getFromComponentId(),
					debugContext.getFromOutSocketId(), clone.getId(), "out0");

			debugContext.getTypeBaseComponents().add(clone);

			return clone;
		}

	},
	LIMIT_COMPONENT {
		@Override
		public TypeBaseComponent create(DebugContext debugContext) {
			Limit limit = new Limit();
			limit.setId(ComponentBuilderUtils.generateUniqueComponentId(
					debugContext.getPreviousComponentId(), "out1",
					debugContext.getTypeBaseComponents()));
			limit.setBatch(debugContext.getBatch());
			MaxRecords mr = new MaxRecords();
			mr.setValue((long) debugContext.getParams().get("limitvalue"));
			limit.setMaxRecords(mr);
			limit.getInSocket().add(
					ComponentBuilderUtils.getStraightPullInSocket(
							debugContext.getPreviousComponentId(), "out1",
							"out"));
			limit.getOutSocket().add(
					ComponentBuilderUtils.getStraightPullOutSocket("out0",
							"in0"));
			debugContext.getTypeBaseComponents().add(limit);
			return limit;
		}
	},
	AVRO_OUTPUT_COMPONENT {
		@Override
		public TypeBaseComponent create(DebugContext debugContext) {
			AvroFile avroOutputFile = new AvroFile();
			avroOutputFile.setId(ComponentBuilderUtils
					.generateUniqueComponentId(
							debugContext.getPreviousComponentId(), "out1",
							debugContext.getTypeBaseComponents()));
			avroOutputFile.setBatch(debugContext.getBatch());
			Path path = new Path();

			String pathUri = debugContext.getBasePath() + "/debug/"
					+ debugContext.getJobId() + "/"
					+ debugContext.getFromComponentId() + "_"
					+ debugContext.getFromOutSocketId();

			path.setUri(pathUri);
			avroOutputFile.setPath(path);

			Set<SchemaField> sf = debugContext.getSchemaFieldsMap().get(
					debugContext.getFromComponentId() + "_"
							+ debugContext.getFromOutSocketId());
			TypeOutputInSocket inSocket = JaxbSchemaFieldConverter
					.convertToJaxb(sf);

			inSocket.setFromComponentId(debugContext.getPreviousComponentId());
			inSocket.setFromSocketId("out0");
			inSocket.setId("in0");
			avroOutputFile.getInSocket().add(inSocket);
			debugContext.getTypeBaseComponents().add(avroOutputFile);
			return avroOutputFile;
		}
	},
	TEXT_OUTPUT_COMPONENT {
		@Override
		public TypeBaseComponent create(DebugContext debugContext) {
			TextFileDelimited textOutputFileDelimited = new TextFileDelimited();
			// set ID
			textOutputFileDelimited.setId(ComponentBuilderUtils
					.generateUniqueComponentId(
							debugContext.getPreviousComponentId(), "out1",
							debugContext.getTypeBaseComponents()));
			// set batch
			textOutputFileDelimited.setBatch(debugContext.getBatch());

			// set path
			String pathUri = debugContext.getBasePath() + "/debug/"
					+ debugContext.getJobId() + "/"
					+ debugContext.getFromComponentId() + "_"
					+ debugContext.getFromOutSocketId();
			TextFileDelimited.Path path = new TextFileDelimited.Path();
			path.setUri(pathUri);
			textOutputFileDelimited.setPath(path);

			// set overwrite option
			TypeTrueFalse value = new TypeTrueFalse();
			value.setValue(TrueFalse.TRUE);
			textOutputFileDelimited.setOverWrite(value);

			// set delimiter
			Delimiter delimiter = new Delimiter();
			delimiter.setValue(",");
			textOutputFileDelimited.setDelimiter(delimiter);

			// set has header
			BooleanValueType hasHeader = new BooleanValueType();
			hasHeader.setValue(true);
			textOutputFileDelimited.setHasHeader(hasHeader);

			// set safe
			BooleanValueType safe = new BooleanValueType();
			safe.setValue(true);
			textOutputFileDelimited.setSafe(safe);

			// set strict
			BooleanValueType strict = new BooleanValueType();
			strict.setValue(false);
			textOutputFileDelimited.setStrict(strict);

			// set charset
			Charset charset = new Charset();
			charset.setValue(StandardCharsets.UTF_8);
			textOutputFileDelimited.setCharset(charset);

			// set quote
			Quote quote = new Quote();
			quote.setValue("\"");
			textOutputFileDelimited.setQuote(quote);

			// set in socket
			Set<SchemaField> sf = debugContext.getSchemaFieldsMap().get(
					debugContext.getFromComponentId() + "_"
							+ debugContext.getFromOutSocketId());
			TypeOutputInSocket inSocket = JaxbSchemaFieldConverter
					.convertToJaxb(sf);
			inSocket.setFromComponentId(debugContext.getPreviousComponentId());
			inSocket.setFromSocketId("out1");
			inSocket.setId("in0");
			textOutputFileDelimited.getInSocket().add(inSocket);

			debugContext.getTypeBaseComponents().add(textOutputFileDelimited);
			return textOutputFileDelimited;
		}
	};

	private final static String viewDataComponentIdentifier = "viewData_";
	
	public abstract TypeBaseComponent create(DebugContext debugContext);

	private static class ComponentBuilderUtils {

		private static TypeBaseInSocket getStraightPullInSocket(
				String formComponentId, String outSocketId, String outSocketType) {
			TypeBaseInSocket baseInSocket = new TypeBaseInSocket();
			baseInSocket.setFromComponentId(formComponentId);
			baseInSocket.setFromSocketId(outSocketId);
			baseInSocket.setFromSocketType(outSocketType);
			baseInSocket.setId("in0");
			return baseInSocket;
		}

		private static TypeStraightPullOutSocket getStraightPullOutSocket(
				String id, String inSocketId) {
			TypeStraightPullOutSocket baseOutSocket = new TypeStraightPullOutSocket();
			baseOutSocket.setId(id);
			TypeOutSocketAsInSocket typeOutSocketAsInSocket = new TypeOutSocketAsInSocket();
			typeOutSocketAsInSocket.setInSocketId(inSocketId);
			baseOutSocket.setCopyOfInsocket(typeOutSocketAsInSocket);
			return baseOutSocket;
		}

		private static String generateUniqueComponentId(String compId,
				String socketId, List<TypeBaseComponent> typeBaseComponents) {
			String newComponentID = compId + "_" + socketId;
			for (int i = 0; i < typeBaseComponents.size(); i++) {
				if (newComponentID.equalsIgnoreCase(typeBaseComponents.get(i)
						.getId())) {
					newComponentID += "_" + i;
				}
			}
			return newComponentID;
		}

		public static TypeBaseComponent getComponent(
				List<TypeBaseComponent> jaxbGraph, String compId,
				String socketId) {
			for (TypeBaseComponent component : jaxbGraph) {
				for (TypeBaseInSocket inSocket : SocketUtilities
						.getInSocketList(component)) {
					if (inSocket.getFromComponentId().equalsIgnoreCase(compId)
							&& inSocket.getFromSocketId().equalsIgnoreCase(
									socketId)) {
						return component;
					}
				}
			}
			throw new RuntimeException("debug FromComponent id: " + compId
					+ " or Socket id: " + socketId
					+ " are not properly configured");
		}

	}
}
