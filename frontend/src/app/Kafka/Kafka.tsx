import React, { useState, useEffect } from 'react';
import { PageSection, Title, Page, PageSectionVariants, Toolbar, ToolbarContent, Gallery, Card, CardHeader, CardTitle, CardBody, CardFooter, Alert } from '@patternfly/react-core';
import axios from 'axios';
import { NodeInfo } from '@app/data/data';

const Kafka: React.FunctionComponent = () => {

  // Nodes
  const [nodes, setNodes] = useState<NodeInfo[]>([]);

  React.useEffect(() => {
    const fetchData = async () => {
      const brokerResponse = await axios.get('/api/node');
      console.log(brokerResponse.data);
      setNodes(brokerResponse.data);
    }
    fetchData();
  }, []);
  
  return (
    <Page>
      <PageSection variant={PageSectionVariants.light}>
        <Title headingLevel="h1" size="lg">Topics</Title>
        <Toolbar id="toolbar-group-types">
          <ToolbarContent></ToolbarContent>
        </Toolbar>
      </PageSection>

      <PageSection>
        <Gallery hasGutter>
          {nodes.map((node, index) => (
            <React.Fragment key={index}>
              <Card isHoverable>
                <CardHeader>
                  <Title headingLevel="h4" size="lg">Node {node.id}</Title>
                </CardHeader>
                <CardBody>
                    <small>{node.host}:{node.port}</small>
                    <small>{node.rack}</small>
                </CardBody>
                <CardFooter>
                <Alert variant="success" isInline title="Running" />
                </CardFooter>
              </Card>
            </React.Fragment>
          ))}
        </Gallery>
      </PageSection>

    </Page>
  )
}

export { Kafka };
