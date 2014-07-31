package org.navy.onr_rc.elasticlda

import org.elasticsearch.common.inject.Inject
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.index.AbstractIndexComponent
import org.elasticsearch.index.Index
import org.elasticsearch.index.mapper.MapperService
import org.elasticsearch.index.settings.IndexSettings

import org.apache.lucene.document.Field
import org.apache.lucene.document.FieldType
import org.elasticsearch.common.io.stream.BytesStreamInput
import org.elasticsearch.common.logging.ESLogger
import org.elasticsearch.common.logging.ESLoggerFactory
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentParser
import org.elasticsearch.index.fielddata.FieldDataType
import org.elasticsearch.index.mapper._
import org.elasticsearch.index.mapper.core.AbstractFieldMapper

// Main workhorse class for managing the type mapping
//
// Notation notes for the Scala newbie:
// (a) AbstractFieldMapper[Class[_ <: Object]] is Scala for Java's AbstractFieldMapper<Object>.

class ElasticLDAMapper(var names : FieldMapper.Names, var pathType : ContentPath.Type, var defaultIndexedChars : Int,
					   var ignoreErrors : Boolean, var defaultLangDetect : Boolean, var contentMapper : Mapper,
                       var dateMapper : Mapper, var titleMapper : Mapper, var nameMapper : Mapper, 
                       var authorMapper : Mapper, var keywordsMapper : Mapper, var contentTypeMapper : Mapper,
                       var contentLengthMapper : Mapper, var languageMapper : Mapper,
                       var multiFields : AbstractFieldMapper.MultiFields, var copyTo : AbstractFieldMapper.CopyTo)
	extends AbstractFieldMapper[Class[_ <: Object]](names, 1.0f, AbstractFieldMapper.Defaults.FIELD_TYPE, false, null, null, null, null, null,
								null, null, ImmutableSettings.EMPTY, multiFields, copyTo)
{

}

class RegisterAttachmentType @Inject() (var index : Index, var indexSettings : Settings,
										var mapperService : MapperService)
	extends AbstractIndexComponent(index, indexSettings)
{
	mapperService.documentMapperParser().putTypeParser("attachment", new ElasticLDAMapper.TypeParser())
}
