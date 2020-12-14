/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */
import {NativeModules} from 'react-native';
import React, {useEffect, useState} from 'react';
import KeyEvent from 'react-native-keyevent';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
} from 'react-native';

import {Header, Colors} from 'react-native/Libraries/NewAppScreen';

declare const global: {HermesInternal: null | {}};
const {ToastyTest} = NativeModules;
const App = () => {
  const [barcode, setBarcode] = useState('');
  useEffect(() => {
    ToastyTest.InitScanner((response: any) => {
      console.log(response);
    });
    KeyEvent.onKeyDownListener((keyEvent: any) => {
      const {keyCode} = keyEvent;
      if (keyCode === 139) {
        ToastyTest.onStartScanner((response: any) => {
          console.log(response);
          setBarcode(response);
        });
      }
    });

    // if you want to react to keyUp
    KeyEvent.onKeyUpListener((keyEvent: any) => {
      const {keyCode} = keyEvent;
      if (keyCode === 139) {
        ToastyTest.onStopScanner();
      }
    });
    return () => {
      KeyEvent.removeKeyDownListener();
      KeyEvent.removeKeyUpListener();
    };
  }, []);
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <Header />
          {global.HermesInternal == null ? null : (
            <View style={styles.engine}>
              <Text style={styles.footer}>Engine: Hermes</Text>
            </View>
          )}
          <View style={styles.body}>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>Scan Result</Text>

              <Text>{`result : ${barcode}`}</Text>
            </View>
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 16,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
