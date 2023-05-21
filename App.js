import React, {useEffect, useState} from 'react';
import {View, TextInput, StyleSheet, NativeModules} from 'react-native';
import SharedGroupPreferences from 'react-native-shared-group-preferences';
import {AppRegistry} from 'react-native';

AppRegistry.registerHeadlessTask('backgroundTask', () =>
  require('./backgroundTask.js'),
);
const SharedStorage = NativeModules.SharedStorage;

const App = () => {
  useEffect(() => {
    NativeModules.BackgroundWorkManager.stopBackgroundWork();
    console.log("STARTING")
    setTimeout(NativeModules.BackgroundWorkManager.startBackgroundWork, 1000);
  }, []);

  const [text, setText] = useState('');
  const widgetData = {
    text,
  };

  const handleSubmit = async () => {
    try {
      // iOS
      // await SharedGroupPreferences.setItem('widgetKey', widgetData, 'group.asap');
    } catch (error) {
      console.log({error});
    }
    // Android
    SharedStorage.set(JSON.stringify({text}));
  };

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        onChangeText={newText => setText(newText)}
        value={text}
        returnKeyType="send"
        onEndEditing={handleSubmit}
        placeholder="Enter the text to display..."
      />
    </View>
  );
};

export default App;

const styles = StyleSheet.create({
  container: {
    marginTop: '50%',
    paddingHorizontal: 24,
  },
  input: {
    width: '100%',
    borderBottomWidth: 1,
    fontSize: 20,
    minHeight: 40,
  },
});