import * as React from 'react';
import { storiesOf } from '@storybook/react';
import { withInfo } from '@storybook/addon-info';
import { Kafka } from '@app/Kafka/Kafka';

const stories = storiesOf('Components', module);
stories.addDecorator(withInfo);
stories.add(
  'Kafka',
  () => <Kafka />,
  { info: { inline: true } }
);
