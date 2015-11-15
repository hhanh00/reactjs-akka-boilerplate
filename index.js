import React, {Component, PropTypes} from 'react'
import ReactDOM from 'react-dom'

import { Router, Route, IndexRoute, Link, IndexLink } from 'react-router'
import { createHistory } from 'history'

import { observeProps, createEventHandler } from 'rx-recompose';
import { mapProps, toClass } from 'recompose';
import { Observable } from 'rx';
import { get } from 'jquery';
import { get as _get } from 'lodash';

const history = createHistory()

const Exposure = (props) => {
  let group = props.group
  return <div>
    <h2>Exposure - {props.group}</h2>
    Data: 
    <ul>
    {props.data && props.data.map(d => {
      let to = `/exposure?${group}=${d.group}`
      if (group == 'team' || group=='strategy')
        return <li key={d.group}><Link to={to}>{d.group}/{d.exposure}</Link></li>
      else
        return <li key={d.group}>{d.group}/{d.exposure}</li>
    })}
    </ul>
    <Link to="/exposure?team=A">Team A</Link>
    <Link to="/exposure?team=B">Team B</Link>
    <Link to="/exposure?team=C">Team C</Link>
  </div>
}

const ExposureContainer = observeProps(props$ => {
  const p$ = props$.map(p => ({g: p.group, t: p.query.team, s: p.query.strategy})).distinctUntilChanged()
  const data$ = p$.flatMap(p => {
    if (p.t)
      return Observable.fromPromise(get(`http://localhost:3000/data?team=${p.t}&group=${p.g}`).promise())
    if (p.s)
      return Observable.fromPromise(get(`http://localhost:3000/data?strategy=${p.s}&group=${p.g}`).promise())
    return Observable.empty
  }).startWith([])

  return Observable.combineLatest(props$, data$, 
    (props, data) => ({data: data, ...props}))
  }, Exposure)

const PnL = (props) => {
  return <div>
    <h2>PnL</h2>
  </div>
}

const App = (props) => {
  return <div>
    <h1>Links</h1>
    <ul>
      <li><Link to="/exposure">Exposure</Link></li>
      <li><Link to="/pnl">P & L</Link></li>
    </ul>
    {props.children}
  </div>
}

const Test = (props) => {
  return <div>
  {props.p}
  </div>
}

const ExposurePanel = (props) => {
  return <div>
  <ExposureContainer group="team" query={props.location.query} />
  <ExposureContainer group="strategy" query={props.location.query} />
  <ExposureContainer group="product" query={props.location.query} />
  </div>
}

const routes = (
  <Route path="/" component={App}>
    <Route path="exposure" component={ExposurePanel} />
    <Route path="pnl" component={PnL} />
  </Route>
)

ReactDOM.render(
  <Router history={history}>
    {routes}
  </Router>
  , 
  document.getElementById('root'))
