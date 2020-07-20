import * as React from 'react';
import { storiesOf } from '@storybook/react';
import { withInfo } from '@storybook/addon-info';
import { Support } from '@app/Topics/Topics';

const stories = storiesOf('Components', module);
stories.addDecorator(withInfo);
stories.add(
  'Topics',
  () => <Topics />,
  { info: { inline: true } }
);
