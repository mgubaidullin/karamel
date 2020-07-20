import React, { useState, useEffect } from 'react';
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
import { ITopicInfo } from '@app/data/data';

export interface ISupportProps {
  sampleProp?: string;
}

const Topics: React.FunctionComponent<ISupportProps> = () => {

  // Broker selector
  const brokerState = {
    isOpen: false,
    selected: null
  };
  const onToggle = isOpen => {
    setBroker({ isOpen: isOpen, selected: broker.selected });
  };

  const onSelect = (event, selection, isPlaceholder) => {
      setBroker({ isOpen: false, selected: selection });
      const fetchData = async () => {
        const response = await axios.get('/api/topic?brokers=' + selection);
        setRows(response.data.map(item => [item.name, item.partitions, String(item.internal)]));
      }
      fetchData();
  };

  const [brokers, setBrokers] = useState([]);
  const [broker, setBroker] = useState(brokerState);

  // Topic table
  const columns = [
    { title: 'Name' },
    { title: 'Partitions', transforms: [textCenter], cellTransforms: [textCenter] },
    { title: 'Internal', transforms: [cellWidth(10)] },
  ]
  const [rows, setRows] = useState<[][]>([]);

  React.useEffect(() => {
    const fetchData = async () => {
      const brokerResponse = await axios.get('/api/broker');
      setBrokers(brokerResponse.data);
    }
    fetchData();
  }, []);

  return (
    <Page>
      <PageSection variant={PageSectionVariants.light}>
        <Toolbar id="toolbar-group-types">
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
        <Table aria-label="Simple Table" cells={columns} rows={rows}>
          <TableHeader className={css(styles.modifiers.nowrap)} />
          <TableBody />
        </Table>
      </PageSection>

    </Page>
  )
}

export { Topics };
