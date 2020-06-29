package org.ohnlp.backbone.core.config;

import java.util.List;

/**
 * This class represents the configuration used for constructing pipelines
 */
public class BackboneConfiguration {
    private String id;
    private String decription;
    private List<BackbonePipelineComponentConfiguration> pipeline;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public List<BackbonePipelineComponentConfiguration> getPipeline() {
        return pipeline;
    }

    public void setPipeline(List<BackbonePipelineComponentConfiguration> pipeline) {
        this.pipeline = pipeline;
    }
}
