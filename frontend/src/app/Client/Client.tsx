import React, { useState, useEffect } from 'react';
import { useEventSource, useEventSourceListener } from "react-use-event-source-ts";
import { CubesIcon } from '@patternfly/react-icons';
import {
  PageSection,
  Text,
  TextContent,
  Title,
  Toolbar,
  ToolbarContent,
  Button,
  EmptyState,
  EmptyStateVariant,
  EmptyStateIcon,
  EmptyStateBody,
  EmptyStateSecondaryActions,
  PageSectionVariants,
  Page,
  Select, SelectOption, SelectVariant, SelectDirection,
  ToolbarItem
} from '@patternfly/react-core';
import {
  Table,
  TableHeader,
  TableBody,
  textCenter,
  cellWidth
} from '@patternfly/react-table';
import { css } from '@patternfly/react-styles';
import { CubeIcon } from '@patternfly/react-icons';
import styles from '@patternfly/react-styles/css/components/Table/table';
import axios from 'axios';
import { ITopicInfo, KEventIn } from '@app/data/data';
import { getSessionId } from '@app/utils/utils';

export interface ISupportProps {
  sampleProp?: string;
}

const Client: React.FunctionComponent<ISupportProps> = () => {

  // Broker selector
  const sessionId = getSessionId(false);
  const brokerState = {
    isOpen: false,
    selected: null
  };
  const onToggle = isOpen => {
    setBroker({ isOpen: isOpen, selected: broker.selected });
  };

  const onSelect = (event, selection, isPlaceholder) => {
    setBroker({ isOpen: false, selected: selection });
    const sendBroker = async () => {
      const response = await axios.post('/api/broker', { brokers: selection, sessionId: sessionId });
    }
    sendBroker();
  };
  const updateProdutList = (product: any) => {
    // setData([...product])
    console.log(product);
  }

  const [brokers, setBrokers] = useState([]);
  const [broker, setBroker] = useState(brokerState);

  // Messages table
  const columns = [
    { title: 'Topic', transforms: [cellWidth(20)] },
    { title: 'Partition', transforms: [cellWidth(10)] },
    { title: 'Offset', transforms: [cellWidth(10)] },
    { title: 'Timestamp', transforms: [cellWidth(20)] },
    { title: 'Key', transforms: [cellWidth(20)] },
    { title: 'Value', transforms: [cellWidth(20)] },
  ]

  React.useEffect(() => {
    const fetchData = async () => {
      const brokerResponse = await axios.get('/api/broker');
      setBrokers(brokerResponse.data);
    }
    fetchData();
  }, []);

  const [messages, setMessages] = useState<KEventIn[]>([]);

  const eventSource = new EventSource('http://localhost:8080/api/message/' + sessionId);
  eventSource.onmessage = function (event) {
    console.log(event.data);
    const m = messages;
    m.push({topic: event.data.topic, partition: event.data.partition, offset: event.data.offset, timestamp: event.data.timestamp, 
      key: event.data.key, value: event.data.value});
    setMessages(m);
  };

  // const [eventSource, eventSourceStatus] = useEventSource('http://localhost:8080/api/message/' + sessionId, true);
  //     useEventSourceListener(eventSource, ['update'], evt => {
  //         console.log(evt.data);
  //         setMessages([
  //             ...messages,
  //             ...JSON.parse(evt.data)
  //         ]);
  //     }, [messages]);


  return (
    <Page>
      <PageSection variant={PageSectionVariants.light}>
        <Toolbar>
          <ToolbarContent>
            <ToolbarItem>
              <Title headingLevel="h1" size="lg">Topics</Title>
            </ToolbarItem>
            <ToolbarItem alignment={{ default: 'alignRight' }}>
              <Select
                variant={SelectVariant.single}
                placeholderText="Select Broker"
                onToggle={onToggle}
                onSelect={onSelect}
                selections={broker.selected}
                isOpen={broker.isOpen}
                direction={SelectDirection.down}
              >
                {brokers.map((b, index) => (
                  <SelectOption
                    isDisabled={false}
                    key={index}
                    value={b}
                    isPlaceholder={false}
                  />
                ))}
              </Select>
            </ToolbarItem>
          </ToolbarContent>
        </Toolbar>
      </PageSection>

      <PageSection>
        <Table aria-label="Simple Table" cells={columns} rows={messages}>
          <TableHeader className={css(styles.modifiers.nowrap)} />
          <TableBody />
        </Table>
      </PageSection>

    </Page>
  )
}

export { Client };
