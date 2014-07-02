package org.navy.onr_rc.elasticlda

import org.elasticsearch.plugins.AbstractPlugin
import org.elasticsearch.common.inject.Module
import java.util.ArrayList
import java.util.Collection

import org.elasticsearch.common.logging.ESLogger
import org.elasticsearch.common.logging.ESLoggerFactory

class ElasticLDAPlugin() extends AbstractPlugin {
  private var logger = ESLoggerFactory.getLogger(this.getClass().getName())

  override def name(): String = "ElasticLDA"
  override def description(): String = "Latent Dirichlet Allocation and Topic Modeling plug-in"
    
  override def indexModules(): Collection[Class[_ <: Module]] = {
    var retval = new ArrayList[Class[_ <: Module]]()
    
    logger.info("elasticlda indexModules invoked.")
    retval
  }
}